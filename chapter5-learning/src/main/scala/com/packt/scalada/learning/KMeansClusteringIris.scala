package com.packt.scalada.learning

import scala.Array.canBuildFrom
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.clustering.KMeansModel

object KMeansClusteringIris extends App {

  val conf = new SparkConf().setAppName("KMeansClusteringIris").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val data = sc.textFile("iris.data").map(line => {
    val dataArray = line.split(",").take(4)
    Vectors.dense(dataArray.map(_.toDouble))
  })

  //Summary statistics before scaling
  val stats = Statistics.colStats(data)
  println("Statistics before scaling")
  print(s"Max : ${stats.max}, Min : ${stats.min}, and Mean : ${stats.mean} and Variance : ${stats.variance}")
  

  //Scale data
  val scaler = new StandardScaler(withMean = true, withStd = true).fit(data)
  val scaledData = scaler.transform(data).cache()

  //Summary statistics before scaling
  val statsAfterScaling = Statistics.colStats(scaledData)
  println("Statistics after scaling")
  print(s"Max : ${statsAfterScaling.max}, Min : ${statsAfterScaling.min}, and Mean : ${statsAfterScaling.mean} and Variance : ${statsAfterScaling.variance}")

  //Take a sample to come up with the number of clusters
  val sampleData = scaledData.sample(false, 0.2).cache()

  //Decide number of clusters
  val clusterCost = (1 to 7).map { noOfClusters =>

    val kmeans = new KMeans()
      .setK(noOfClusters)
      .setMaxIterations(5)
      .setInitializationMode(KMeans.K_MEANS_PARALLEL) //KMeans||

    val model = kmeans.run(scaledData)

    (noOfClusters, model.computeCost(scaledData))

  }

  println ("Cluster cost on sample data " )
  clusterCost.foreach(println)

  //Let's do the real run for 3 clusters
  val kmeans = new KMeans()
    .setK(3)
    .setMaxIterations(5)
    .setInitializationMode(KMeans.K_MEANS_PARALLEL) //KMeans||

  val model = kmeans.run(scaledData)

  //Cost 
  println("Total cost " + model.computeCost(sampleData))
  printClusterCenters(model)

  def printClusterCenters(model:KMeansModel) {
    //Cluster centers
    val clusterCenters: Array[Vector] = model.clusterCenters
    println("Cluster centers")
    clusterCenters.foreach(println)

  }

}



  