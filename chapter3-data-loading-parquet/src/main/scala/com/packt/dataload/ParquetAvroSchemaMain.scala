package com.packt.dataload

import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.packt.dataload.model.caseclass.Student
import scala.io.Source
import org.apache.spark.SparkContext._
import com.packt.dataload.model.caseclass.Student
import org.apache.spark.sql.DataFrame

object ParquetAvroSchemaMain extends App{
  
  val conf = new SparkConf().setAppName("AvroModelToParquet").setMaster("local[2]")

  val sc = new SparkContext(conf)

  val sqlContext = new SQLContext(sc)

  import sqlContext.implicits._
  

}

