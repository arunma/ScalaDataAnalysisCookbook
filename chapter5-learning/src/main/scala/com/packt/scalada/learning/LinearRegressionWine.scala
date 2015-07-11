
package com.packt.scalada.learning

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.optimization.SquaredL2Updater
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.regression.LassoWithSGD
import org.apache.spark.mllib.regression.RidgeRegressionWithSGD
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.mllib.regression.GeneralizedLinearModel

object LinearRegressionWine extends App {

  val conf = new SparkConf().setAppName("linearRegressionWine").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val rdd = sc.textFile("winequality-red.csv").map(line => line.split(";"))

  //Summary stats
  val featureVector = rdd.map(row => Vectors.dense(row.take(11).map(str => str.toDouble)))
  val stats = Statistics.colStats(featureVector)
  print (s"Max : ${stats.max}, Min : ${stats.min}, and Mean : ${stats.mean}")

  val dataPoints = rdd.map(row => new LabeledPoint(row.last.toDouble, Vectors.dense(row.take(11).map(str => str.toDouble)))).cache()

  //split dataset
  val splits = dataPoints.randomSplit(Array(0.8, 0.2))
  val trainingSplit = splits(0)
  val testSplit = splits(1)

  //Let's scale the points. 
  val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainingSplit.map(dp => dp.features))
  val scaledTrainingSplit = trainingSplit.map(dp => new LabeledPoint(dp.label, scaler.transform(dp.features)))
  val scaledTestSplit = testSplit.map(dp => new LabeledPoint(dp.label, scaler.transform(dp.features)))

  val iterations = 1000
  val stepSize = 1

  //Create models
  val linearRegWithoutRegularization=algorithm("linear", iterations, stepSize)
  val linRegressionPredictActuals = runRegression(linearRegWithoutRegularization)

  val lasso=algorithm("lasso", iterations, stepSize)
  val lassoPredictActuals = runRegression(lasso)

  val ridge=algorithm("ridge", iterations, stepSize)
  val ridgePredictActuals = runRegression(ridge)

  //Calculate evaluation metrics
  calculateMetrics(linRegressionPredictActuals, "Linear Regression with SGD")
  calculateMetrics(lassoPredictActuals, "Lasso Regression with SGD")
  calculateMetrics(ridgePredictActuals, "Ridge Regression with SGD")
  
  
  def algorithm(algo: String, iterations: Int, stepSize: Double) = algo match {
    case "linear" => {
      val algo = new LinearRegressionWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setMiniBatchFraction(0.5)
      algo
    }
    case "lasso" => {
      val algo = new LassoWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(0.001).setMiniBatchFraction(0.5)
      algo
    }
    case "ridge" => {
      val algo = new RidgeRegressionWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(0.001).setMiniBatchFraction(0.5)
      algo
    }
  }

  def runRegression(algorithm: GeneralizedLinearAlgorithm[_ <: GeneralizedLinearModel]):RDD[(Double,Double)] = {
    val model = algorithm.run(scaledTrainingSplit) //Let's pass in the training split 

    val predictions: RDD[Double] = model.predict(scaledTestSplit.map(point => point.features))
    val actuals: RDD[Double] = scaledTestSplit.map(point => point.label)

    //Let's go ahead and calculate the Residual Sum of squares
    val predictsAndActuals: RDD[(Double, Double)] = predictions.zip(actuals)
    predictsAndActuals
  }

  def calculateMetrics(predictsAndActuals: RDD[(Double, Double)], algorithm: String) {

    val sumSquaredErrors = predictsAndActuals.map {
      case (pred, act) =>
        math.pow(act - pred, 2)
    }.sum()

    val meanPrice = dataPoints.map(point => point.label).mean()

    val totalSumOfSquares = predictsAndActuals.map {
      case (pred, act) =>
        math.pow(act - meanPrice, 2)
    }.sum()

    val meanSquaredError = sumSquaredErrors / scaledTestSplit.count

    println(s"************** Printing metrics for $algorithm *****************")

    println(s"SSE is $sumSquaredErrors")
    println(s"MSE is $meanSquaredError")

    println(s"SST is $totalSumOfSquares")
    val rss = 1 - sumSquaredErrors / totalSumOfSquares

    println(s"Residual sum of squares is $rss")

    println(s"************** ending metrics for $algorithm *****************")

  }
  
  

}