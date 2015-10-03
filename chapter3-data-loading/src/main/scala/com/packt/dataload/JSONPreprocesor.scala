package com.packt.dataload

import java.sql.Timestamp

import scala.reflect.runtime.universe

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.joda.time.format.DateTimeFormat
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.compact
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.JsonMethods.render
import org.json4s.jvalue2monadic
import org.json4s.string2JsonInput

case class JsonDateModel (name:String, dob:Timestamp, tags:String)

object JSONPreprocesor extends App {

  val conf = new SparkConf().setAppName("DFramePrpe").setMaster("local[1]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  //Print all distinct by one field alone

  //Preprocessing JSON
  val stringRDD = sc.textFile("StrangeDate.json")

  //Assume that you would want to parse and treat the date as a date format because jsonFile considers only dates in ISO 8601 format to be date and considers all else to be String.
  //Performing arbitrary transformations is a work in progress (https://issues.apache.org/jira/browse/SPARK-4190).  Until then, let's give it a shot 
  import org.json4s._
  import org.json4s.jackson.JsonMethods._
  implicit val formats = DefaultFormats
  val formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")
  
  val dateModelRDD = for {
    json <- stringRDD
    jsonValue = parse(json)
    name = compact(render(jsonValue \ "name"))
    dateAsString=compact(render(jsonValue \ "dob")).replace("\"","")
    date = new Timestamp(formatter.parseDateTime(dateAsString).getMillis())
    tags = render(jsonValue \ "tags").extract[List[String]].mkString(",")
  } yield JsonDateModel(name, date, tags)

  import sqlContext.implicits._
  val df=dateModelRDD.toDF()
  df.printSchema()
  df.show(df.count.toInt)
  
}