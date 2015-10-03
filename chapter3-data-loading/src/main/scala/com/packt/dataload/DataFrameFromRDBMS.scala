package com.packt.dataload

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv._
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql.CassandraConnector
import scala.io.Source
import com.typesafe.config.ConfigFactory

object DataFrameFromRDBMS extends App {

  val conf = new SparkConf().setAppName("DataFromRDBMS").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val config = ConfigFactory.load()

  val options = Map(
    "driver" -> config.getString("mysql.driver"),
    "url" -> config.getString("mysql.connection.url"),
    "dbtable" -> "(select * from student) as student",
    "partitionColumn" -> "id",
    "lowerBound" -> "1",
    "upperBound" -> "100",
  	"numPartitions"-> "2")
    
  val dFrame=sqlContext.load("jdbc", options)
  
  dFrame.printSchema()
  
  dFrame.show()
  
  
  
}