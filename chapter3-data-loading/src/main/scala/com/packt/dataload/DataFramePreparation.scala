package com.packt.dataload

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark._
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv.CsvContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import scala.util.Try
import org.apache.spark.sql.types.DateType
import org.json4s.JObject


object DataFramePreparation extends App{
  
  val conf = new SparkConf().setAppName("DFramePrpe").setMaster("local[1]")
  val sc = new SparkContext(conf)
  val sqlContext=new SQLContext(sc)

  val students1=sqlContext.csvFile(filePath="StudentPrep1.csv", useHeader=true, delimiter='|')
  val students2=sqlContext.csvFile(filePath="StudentPrep2.csv", useHeader=true, delimiter='|')
  
//  1. Combining two or more DataFrames
  val allStudents=students1.unionAll(students2)
  println ("union")
  allStudents.foreach(println)
  
  //2. intersection
  val intersection=students1.intersect(students2)
  println ("intersection")
  intersection.foreach(println)
  
  //3. Print all distinct
  val distinctStudents=allStudents.distinct
  println ("distinct")
  println(distinctStudents.count())
  
  //4.Subtraction
  val subtraction=students1.except(students2)
  println ("subtraction")
  subtraction.foreach(println)

  //Sort by Id - Treating it a number 
  val sortByIdRdd=allStudents.rdd.map(eachRow=>(Try(eachRow.getString(0).toInt).getOrElse(Int.MaxValue),eachRow)).sortByKey(true)
  println ("sorting")
  sortByIdRdd.foreach(println)
  
  //Removes duplicates by id and holds on to the row with the longest name
  val idStudentPairs=allStudents.rdd.map(eachRow=>(eachRow.getString(0),eachRow))
  val longestNameRdd=idStudentPairs.reduceByKey((row1, row2) =>
  	if (row1.getString(1).length()>row2.getString(1).length()) row1 else row2
  )

  longestNameRdd.foreach(println)
  
}