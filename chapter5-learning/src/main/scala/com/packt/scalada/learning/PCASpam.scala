package com.packt.scalada.learning

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.feature.IDFModel
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import epic.preprocess.MLSentenceSegmenter
import epic.preprocess.TreebankTokenizer
import org.apache.spark.mllib.regression.GeneralizedLinearModel
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors

object PCASpam extends App {

  val conf = new SparkConf().setAppName("PCASpam").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  case class Document(label: String, content: String)

  val docs = sc.textFile("SMSSpamCollection").map(line => {
    val words = line.split("\t")
    Document(words.head.trim(), words.tail.mkString(" "))
  }).cache()

  val labeledPointsWithTf = getLabeledPoints(docs)
  val lpTfIdf = withIdf(labeledPointsWithTf).cache()

  //Split dataset
  val spamPoints = lpTfIdf.filter(point => point.label == 1).randomSplit(Array(0.8, 0.2))
  val hamPoints = lpTfIdf.filter(point => point.label == 0).randomSplit(Array(0.8, 0.2))

  println("Spam count:" + (spamPoints(0).count) + "::" + (spamPoints(1).count))
  println("Ham count:" + (hamPoints(0).count) + "::" + (hamPoints(1).count))

  val trainingSpamSplit = spamPoints(0)
  val testSpamSplit = spamPoints(1)

  val trainingHamSplit = hamPoints(0)
  val testHamSplit = hamPoints(1)

  val trainingSplit = trainingSpamSplit ++ trainingHamSplit
  val testSplit = testSpamSplit ++ testHamSplit

  println ("Training split count : "+trainingSplit.count())
  println ("Test split count : "+testSplit.count())

  //Unscaled sample. Watch out. However, the actual dimension reduction is done on scaled data
 /* val dimensionDecidingSample=new RowMatrix((trainingSplit.randomSplit(Array(0.8,0.2))(1)).map(lp=>lp.features))
  val svd = dimensionDecidingSample.computeSVD(100, computeU = false)
   val sum = svd.s.toArray.sum
  //Calculate the number of principal components which could retain a variance of 95%
  val featureRange=(0 to 1000)
  
  val placeholder=svd.s.toArray.zip(featureRange).foldLeft(0.0) {
    case (cum, (curr, component)) =>
      val percent = (cum + curr) / sum
      println(s"Component and percent ${component + 1} :: $percent :::: Singular value is : $curr")
      cum + curr
  }*/


  val unlabeledTrainData = trainingSplit.map(lpoint => Vectors.dense(lpoint.features.toArray)).cache()
  
  //Scale data - Does not support scaling of SparseVector.  
  val scaler = new StandardScaler(withMean = true, withStd = false).fit(unlabeledTrainData)
  val scaledTrainingData = scaler.transform(unlabeledTrainData).cache()
  
  val trainMatrix = new RowMatrix(scaledTrainingData)
  val pcomp: Matrix = trainMatrix.computePrincipalComponents(100) 
  
  println ("trainMatrix dimension  "+ trainMatrix.numRows +"::"+trainMatrix.numCols)
  println ("Pcomp dimension  "+ pcomp.numRows +"::"+pcomp.numCols)
  
  val reducedTrainingData = trainMatrix.multiply(pcomp).rows.cache()
  val reducedTrainingSplit = trainingSplit.zip(reducedTrainingData).map { case (labeled, reduced) => new LabeledPoint(labeled.label, reduced) }
  
  val unlabeledTestData=testSplit.map(lpoint=>lpoint.features)
  val testMatrix = new RowMatrix(unlabeledTestData)
  val reducedTestData=testMatrix.multiply(pcomp).rows.cache()
  val reducedTestSplit=testSplit.zip(reducedTestData).map{case (labeled,reduced) => new LabeledPoint (labeled.label, reduced)}
  

  //println("Reduced Data to debug string " + reducedTrainingData.toDebugString)
  //Get the principal components
  
  val logisticWithBFGS = getAlgorithm(10, 1, 0.001)
  val logisticWithBFGSPredictsActuals = runClassification(logisticWithBFGS, reducedTrainingSplit, reducedTestSplit)
  calculateMetrics(logisticWithBFGSPredictsActuals, "Logistic with BFGS")

  def getAlgorithm(iterations: Int, stepSize: Double, regParam: Double) = {
    val algo = new LogisticRegressionWithLBFGS()
    algo.setIntercept(true).optimizer.setNumIterations(iterations).setRegParam(regParam)
    algo
  }

  def runClassification(algorithm: GeneralizedLinearAlgorithm[_ <: GeneralizedLinearModel], trainingData: RDD[LabeledPoint],
    testData: RDD[LabeledPoint]): RDD[(Double, Double)] = {
    val model = algorithm.run(trainingData)
    println ("predicting")
    val predicted = model.predict(testData.map(point => point.features))
    val actuals = testData.map(point => point.label)
    val predictsAndActuals: RDD[(Double, Double)] = predicted.zip(actuals)
    println (predictsAndActuals.collect)
    predictsAndActuals
  }

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
  
  def withIdf(lPoints: RDD[LabeledPoint]): RDD[LabeledPoint] = {
    val hashedFeatures = labeledPointsWithTf.map(lp => lp.features)
    val idf: IDF = new IDF()
    val idfModel: IDFModel = idf.fit(hashedFeatures)

    val tfIdf: RDD[Vector] = idfModel.transform(hashedFeatures)

    val lpTfIdf = labeledPointsWithTf.zip(tfIdf).map {
      case (originalLPoint, tfIdfVector) => {
        new LabeledPoint(originalLPoint.label, tfIdfVector)
      }
    }

    lpTfIdf
  }

}
  