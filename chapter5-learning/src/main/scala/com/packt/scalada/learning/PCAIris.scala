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
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
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
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.mllib.linalg.Matrices

object PCAIris extends App {

  val conf = new SparkConf().setAppName("PCAIris").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val data = sc.textFile("iris.data").map(line => {
    val dataArray = line.split(",").take(4)
    Vectors.dense(dataArray.map(_.toDouble))
  })

  //Scale data
  val scaler = new StandardScaler(withMean = true, withStd = false).fit(data)
  val scaledData = scaler.transform(data).cache()

  println("Count" + scaledData.count)

  val matrix = new RowMatrix(scaledData)
  val svd = matrix.computeSVD(3)

  val sum = svd.s.toArray.sum

  svd.s.toArray.zipWithIndex.foldLeft(0.0) {
    case (cum, (curr, component)) =>
      val percent = (cum + curr) / sum
      println(s"Component and percent ${component + 1} :: $percent :::: Singular value is : $curr")
      cum + curr
  }

  val pcomp: Matrix = matrix.computePrincipalComponents(3)
  val reducedData = matrix.multiply(pcomp).rows

  //println(s"Rows * Columns :${projectedData.numRows} * ${projectedData.numCols}")

  //principalComponents.

  //Decide number of clusters
  val clusterCost = (1 to 7).map { noOfClusters =>
    val kmeans = new KMeans()
      .setK(noOfClusters)
      .setMaxIterations(5)
      .setInitializationMode(KMeans.K_MEANS_PARALLEL) //KMeans||

    val model = kmeans.run(reducedData)
    (noOfClusters, model.computeCost(reducedData))
  }

  println("Cluster cost on sample data ")
  clusterCost.foreach(println)

}
  