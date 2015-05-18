package com.packt.dataload

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.types._
import scala.reflect.io.File
import scala.io.Source

object DataFrameFromJSON extends App {

  val conf = new SparkConf().setAppName("DataFromJSON").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  //val dFrame=sqlContext.jsonFile("/Users/Gabriel/Dropbox/arun/ScalaDataAnalysis/Code/scala-dataanalysis-cookbook/chapter3-data-loading/profiles.json")

  val dFrame = sqlContext.jsonFile("hdfs://localhost:9000/data/scalada/profiles.json")
  dFrame.printSchema()
  dFrame.show()

  //Using JSONRDD
  val strRDD = sc.textFile("hdfs://localhost:9000/data/scalada/profiles.json")
  val jsonRDD = sqlContext.jsonRDD(strRDD)

  jsonRDD.printSchema()
  jsonRDD.show()

  //Explicit Schema Definition
  val profilesSchema = StructType(
    Seq(
      StructField("id", StringType, true),
      StructField("about", StringType, true),
      StructField("address", StringType, true),
      StructField("age", IntegerType, true),
      StructField("company", StringType, true),
      StructField("email", StringType, true),
      StructField("eyeColor", StringType, true),
      StructField("favoriteFruit", StringType, true),
      StructField("gender", StringType, true),
      StructField("name", StringType, true),
      StructField("phone", StringType, true),
      StructField("registered", TimestampType, true),
      StructField("tags", ArrayType(StringType), true)))

  val jsonRDDWithSchema = sqlContext.jsonRDD(strRDD, profilesSchema)

  jsonRDDWithSchema.printSchema() //Has timestamp
  jsonRDDWithSchema.show()

  jsonRDDWithSchema.registerTempTable("profilesTable")

  //Filter based on timestamp
  val filterCount = sqlContext.sql("select * from profilesTable where registered> CAST('2014-08-26 00:00:00' AS TIMESTAMP)").count

  val fullCount = sqlContext.sql("select * from profilesTable").count

  println("All Records Count : " + fullCount) //200
  println("Filtered based on timestamp count : " + filterCount) //106

  //Writes schema as JSON to file
  File("profileSchema.json").writeAll(profilesSchema.json)

  val loadedSchema = DataType.fromJson(Source.fromFile("profileSchema.json").mkString)
  //Print loaded schema
  println(loadedSchema.prettyJson)

}