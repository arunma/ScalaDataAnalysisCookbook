package com.scalada.goingfurther

import scala.collection.JavaConversions._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.elasticsearch.spark.sql.sqlContextFunctions
import scala.collection.mutable.Buffer
import org.apache.spark.graphx._

object TwitterGraphX {

  def main(args: Array[String]) {

    val conf = new SparkConf()
      .setAppName("TwitterGraphX")
      .setMaster("local[2]")
      .set(ConfigurationOptions.ES_NODES, "localhost") //Default is localhost. Point to ES node when required
      .set(ConfigurationOptions.ES_PORT, "9200")

    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val twitterStatusDf = convertElasticSearchDataToDataFrame(sqlContext)
    
    val graph=convertDataFrameToGraph(twitterStatusDf)
    
    val topConnectedHashTags=getHashTagsOfTopConnectedComponent(graph)
    
    saveTopTags(topConnectedHashTags)

  }

  def convertElasticSearchDataToDataFrame(sqlContext: SQLContext) = {
    val twStatusDf = sqlContext.esDF("spark/twstatus")
    twStatusDf
  }

  def convertDataFrameToGraph(df: DataFrame):Graph[String,String]= {

    val verticesRdd:RDD[(Long,String)] = df.flatMap { tweet =>
      val hashTags = tweet.getAs[Buffer[String]]("hashTags")
      hashTags.map { tag =>
        val lowercaseTag = tag.toLowerCase()
        val tagHashCode=lowercaseTag.hashCode().toLong
        (tagHashCode, lowercaseTag)
      }
    }
    
    
    val edgesRdd:RDD[Edge[String]] = df.flatMap { row =>
      val hashTags = row.getAs[Buffer[String]]("hashTags")
      
      val urls = row.getAs[Buffer[String]]("urls")
      val topUrl=if (urls.length>0) urls(0) else ""
      
      val combinations=hashTags.combinations(2)
      
      combinations.map{ combs=>
        val firstHash=combs(0).toLowerCase().hashCode.toLong
        val secondHash=combs(1).toLowerCase().hashCode.toLong
        Edge(firstHash, secondHash, topUrl)
      }
    }
    
    val graph=Graph(verticesRdd, edgesRdd)
    
    println ("Sample Vertices")
    graph.vertices.take(20).foreach(println)
    
    println ("Sample Edges")
    graph.edges.take(20).foreach(println)
    
    println ("Sample Triplets")
    graph.triplets.take(20).foreach(println)
    
    graph.cache()
  }
  
  def getHashTagsOfTopConnectedComponent(graph:Graph[String,String]):RDD[String]={
    //Get all the connected components
    val connectedComponents=graph.connectedComponents.cache()
    
    import scala.collection._
    
    val ccCounts:Map[VertexId, Long]=connectedComponents.vertices.map{case (_, vertexId) => vertexId}.countByValue

    //Get the top component Id and count
    val topComponent:(VertexId, Long)=ccCounts.toSeq.sortBy{case (componentId, count) => count}.reverse.head
    
    //RDD of HashTag-Component Id pair. Joins using vertexId
    val hashtagComponentRdd:VertexRDD[(String,VertexId)]=graph.vertices.innerJoin(connectedComponents.vertices){ case (vertexId, hashTag, componentId)=>
      (hashTag, componentId)
    }
    
    //Filter the vertices that belong to the top component alone
    val topComponentHashTags=hashtagComponentRdd
    				.filter{ case (vertexId, (hashTag, componentId)) => (componentId==topComponent._1)}
    				.map{case (vertexId, (hashTag,componentId)) => hashTag
    }
    
    topComponentHashTags
    
  }
  
  
  def saveTopTags(topTags:RDD[String]){
    topTags.repartition(1).saveAsTextFile("topTags.txt")
  }

}