package com.packt.scalada.learning

import java.util.Properties
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.io.Source
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.optimization.Updater
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.GeneralizedLinearModel
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.mllib.optimization.SquaredL2Updater
import epic.preprocess.TreebankTokenizer
import epic.preprocess.MLSentenceSegmenter
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.feature.IDFModel
import org.apache.spark.ml.feature.Tokenizer

object BinaryClassificationSpamMesos extends App {

  val conf = new SparkConf()
  							.setAppName("BinaryClassificationSpamMesos")
		  					//.setMaster("mesos://localhost:5050")
		  					
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  //Frankly, we could make this a tuple but this looks neat
  case class Document(label: String, content: String)

  val docs = sc.textFile("hdfs://localhost:9000/scalada/SMSSpamCollection").map(line => {
    val words = line.split("\t")
    Document(words.head.trim(), words.tail.mkString(" "))
  })

  
  val labeledPointsWithTf=getLabeledPoints(docs)
   val lpTfIdf=withIdf(labeledPointsWithTf).cache()
  
  //Split dataset
  val spamPoints = lpTfIdf.filter(point => point.label == 1).randomSplit(Array(0.8, 0.2))
  val hamPoints = lpTfIdf.filter(point => point.label == 0).randomSplit(Array(0.8, 0.2))

  println("Spam count:" + (spamPoints(0).count) + "::" + (spamPoints(1).count))
  println("Ham count:" + (hamPoints(0).count) + "::" + (hamPoints(1).count))

  val trainingSpamSplit = spamPoints(0)
  val testSpamSplit = spamPoints(1)

  val trainingHamSplit = hamPoints(0)
  val testHamSplit = hamPoints(1)

  val trainingSplit = (trainingSpamSplit ++ trainingHamSplit).cache()
  val testSplit = (testSpamSplit ++ testHamSplit).cache()

  val logisticWithSGD = getAlgorithm("LOGSGD", 100, 1, 0.001)
  val logisticWithBfgs = getAlgorithm("LOGBFGS", 100, 1, 0.001)
  val svmWithSGD = getAlgorithm("SVMSGD", 100, 1, 0.001)

  //val logisticWithSGDPredictsActuals=runClassification(logisticWithSGD, trainingSplit, testSplit)
  val logisticWithBfgsPredictsActuals = runClassification(logisticWithBfgs, trainingSplit, testSplit)
  //val svmWithSGDPredictsActuals=runClassification(svmWithSGD, trainingSplit, testSplit)

  //Calculate evaluation metrics
  //calculateMetrics(logisticWithSGDPredictsActuals, "Logistic Regression with SGD")
  calculateMetrics(logisticWithBfgsPredictsActuals, "Logistic Regression with BFGS")
  //calculateMetrics(svmWithSGDPredictsActuals, "SVM with SGD")
  

  def getAlgorithm(algo: String, iterations: Int, stepSize: Double, regParam: Double) = algo match {
    case "LOGSGD" => {
      val algo = new LogisticRegressionWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(regParam)
      algo
    }
    case "LOGBFGS" => {
      val algo = new LogisticRegressionWithLBFGS()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setRegParam(regParam)
      algo
    }
    case "SVMSGD" => {
      val algo = new SVMWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(regParam)
      algo
    }
  }


  /*labeledPointsWithTf.foreach(lp=>{
    println (lp.label +" features : "+lp.features)
  
  })*/

  def withIdf(lPoints: RDD[LabeledPoint]): RDD[LabeledPoint] = {
    val hashedFeatures = labeledPointsWithTf.map(lp => lp.features)
    val idf: IDF = new IDF()
    val idfModel: IDFModel = idf.fit(hashedFeatures)

    val tfIdf: RDD[Vector] = idfModel.transform(hashedFeatures)
    
    val lpTfIdf= labeledPointsWithTf.zip(tfIdf).map {
      case (originalLPoint, tfIdfVector) => {
        new LabeledPoint(originalLPoint.label, tfIdfVector)
      }
    }
    
    lpTfIdf
  }
 
  
  def withNormalization(lPoints: RDD[LabeledPoint]): RDD[LabeledPoint] = {
    
    val tfIdf: RDD[Vector] = labeledPointsWithTf.map(lp => lp.features)
    
    val normalizer = new Normalizer()
    val lpTfIdfNormalized = labeledPointsWithTf.zip(tfIdf).map {
      case (originalLPoint, tfIdfVector) => {
        new LabeledPoint(originalLPoint.label, normalizer.transform(tfIdfVector))
      }
    }
    lpTfIdfNormalized
  }
  
 

  def runClassification(algorithm: GeneralizedLinearAlgorithm[_ <: GeneralizedLinearModel], trainingData: RDD[LabeledPoint], testData: RDD[LabeledPoint]): RDD[(Double, Double)] = {
    val model = algorithm.run(trainingData)
    val predicted = model.predict(testData.map(point => point.features))
    val actuals = testData.map(point => point.label)
    val predictsAndActuals: RDD[(Double, Double)] = predicted.zip(actuals)
    predictsAndActuals
  }

  def calculateMetrics(predictsAndActuals: RDD[(Double, Double)], algorithm: String) {

    val accuracy = 1.0 * predictsAndActuals.filter(predActs => predActs._1 == predActs._2).count() / predictsAndActuals.count()
    val binMetrics = new BinaryClassificationMetrics(predictsAndActuals)
    println(s"************** Printing metrics for $algorithm ***************")
    println(s"Area under ROC ${binMetrics.areaUnderROC}")
    //println(s"Accuracy $accuracy")
    
    val metrics = new MulticlassMetrics(predictsAndActuals)
    val f1=metrics.fMeasure
    println(s"F1 $f1")
    
    println(s"Precision : ${metrics.precision}")
    println(s"Confusion Matrix \n${metrics.confusionMatrix}")
    println(s"************** ending metrics for $algorithm *****************")
  }

  

   def getLabeledPoints(docs: RDD[Document]): RDD[LabeledPoint] = {

      //Use Scala NLP - Epic
      val labeledPointsUsingEpicRdd: RDD[LabeledPoint] = docs.mapPartitions { docIter =>

        val segmenter = MLSentenceSegmenter.bundled().get
        val tokenizer = new TreebankTokenizer()
        val hashingTf = new HashingTF(5000)

        docIter.map { doc =>
          val sentences = segmenter.apply(doc.content)
          val features = sentences.flatMap(sentence => tokenizer(sentence))

          //consider only features that are letters or digits and cut off all words that are less than 2 characters
          features.toList.filter(token => token.forall(_.isLetterOrDigit)).filter(_.length() > 1)

          new LabeledPoint(if (doc.label.equals("ham")) 0 else 1, hashingTf.transform(features))
        }
      }.cache()

      labeledPointsUsingEpicRdd

  }
  
}
  