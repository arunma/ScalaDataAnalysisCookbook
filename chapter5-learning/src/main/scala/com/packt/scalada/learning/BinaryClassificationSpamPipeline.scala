package com.packt.scalada.learning

import scala.reflect.runtime.universe
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.tuning.CrossValidator
import org.apache.spark.ml.tuning.ParamGridBuilder
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics

object BinaryClassificationSpamPipeline extends App {

  val conf = new SparkConf().setAppName("BinaryClassificationSpamPipeline").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  case class Document(label: Double, content: String)

  val docs = sc.textFile("SMSSpamCollection").map(line => {
    val words = line.split("\t")
    val label=if (words.head.trim().equals("spam")) 1.0 else 0.0
    Document(label, words.tail.mkString(" "))
  })
  
  //Split dataset
  val spamPoints = docs.filter(doc => doc.label==1.0).randomSplit(Array(0.8, 0.2))
  val hamPoints = docs.filter(doc => doc.label==0.0).randomSplit(Array(0.8, 0.2))

  println("Spam count:" + (spamPoints(0).count) + "::" + (spamPoints(1).count))
  println("Ham count:" + (hamPoints(0).count) + "::" + (hamPoints(1).count))

  val trainingSpamSplit = spamPoints(0)
  val testSpamSplit = spamPoints(1)

  val trainingHamSplit = hamPoints(0)
  val testHamSplit = hamPoints(1)

  val trainingSplit = trainingSpamSplit ++ trainingHamSplit
  val testSplit = testSpamSplit ++ testHamSplit

  //Convert documents to Dataframe because the cross validator needs a dataframe
  import sqlContext.implicits._
  val trainingDFrame=trainingSplit.toDF()
  val testDFrame=testSplit.toDF()
  
  val tokenizer=new Tokenizer().setInputCol("content").setOutputCol("tokens")
  val hashingTf=new HashingTF().setInputCol(tokenizer.getOutputCol).setOutputCol("features")
  val logisticRegression=new LogisticRegression().setFeaturesCol(hashingTf.getOutputCol).setLabelCol("label").setMaxIter(10)
  
  
  val pipeline=new Pipeline()
  pipeline.setStages(Array(tokenizer, hashingTf, logisticRegression))
  val model=pipeline.fit(trainingDFrame)
  
  //No cross validation
  val predictsAndActualsNoCV:RDD[(Double,Double)]=model.transform(testDFrame)
      .select("content", "label", "probability", "prediction")
      .map {
      		case Row(content: String, label:Double, prob: Vector, prediction: Double) =>  (label,prediction)
  		}.cache()
  
  calculateMetrics(predictsAndActualsNoCV, "Without Cross validation")
  
  
  //Using Cross validator
  
  //This will provide the cross validator various parameters to choose from 	
  val paramGrid=new ParamGridBuilder()
  	.addGrid(hashingTf.numFeatures, Array(1000, 5000, 10000))
  	.addGrid(logisticRegression.regParam, Array(1, 0.1, 0.03, 0.01))
  	.build()
  	
  val crossValidator=new CrossValidator()
  	.setEstimator(pipeline)
  	.setEvaluator(new BinaryClassificationEvaluator())
  	.setEstimatorParamMaps(paramGrid)
  	.setNumFolds(10)

  val bestModel=crossValidator.fit(trainingDFrame)
  
  val predictsAndActualsWithCV:RDD[(Double,Double)]=model.transform(testDFrame)
      .select("content", "label", "probability", "prediction")
      .map {
      		case Row(text: String, label:Double, prob: Vector, prediction: Double) => (label,prediction)
  		}.cache()

  calculateMetrics(predictsAndActualsWithCV, "Cross validation")


  def calculateMetrics(predictsAndActuals: RDD[(Double, Double)], algorithm: String) {

    val accuracy = 1.0 * predictsAndActuals.filter(predActs => predActs._1 == predActs._2).count() / predictsAndActuals.count()
    val binMetrics = new BinaryClassificationMetrics(predictsAndActuals)
    println(s"************** Printing metrics for $algorithm ***************")
    println(s"Area under ROC ${binMetrics.areaUnderROC}")
    println(s"Accuracy $accuracy")

    val metrics = new MulticlassMetrics(predictsAndActuals)
    println(s"Precision : ${metrics.precision}")
    println(s"Confusion Matrix \n${metrics.confusionMatrix}")
    println(s"************** ending metrics for $algorithm *****************")
  }

  

}
  