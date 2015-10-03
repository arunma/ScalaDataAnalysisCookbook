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
  allStudents.show(allStudents.count().toInt)
  
  //2. intersection
  val intersection=students1.intersect(students2)
  println ("intersection")
  intersection.foreach(println)
  
  //3. Print all distinct
  val distinctStudents=allStudents.distinct
  println ("distinct")
  distinctStudents.foreach(println)
  println(distinctStudents.count())
  
  //4.Subtraction
  val subtraction=students1.except(students2)
  println ("subtraction")
  subtraction.foreach(println)

  //Sort by Id - Treating it a number 
  val sortedCols=allStudents.selectExpr("cast(id as int) as id", "studentName", "phone", "email").sort("id")
  println ("sorting")
  sortedCols.show(sortedCols.count.toInt)
  
  //Removes duplicates by id and holds on to the row with the longest name
  val idStudentPairs=allStudents.rdd.map(eachRow=>(eachRow.getString(0),eachRow))
  val longestNameRdd=idStudentPairs.reduceByKey((row1, row2) =>
  	if (row1.getString(1).length()>row2.getString(1).length()) row1 else row2
  )

  longestNameRdd.values.foreach(println)
  
  
    //Inner Join
  val studentsJoin=students1.join(students2, students1("id")===students2("id"))
  studentsJoin.show(studentsJoin.count.toInt)

  //Left outer join
  val studentsLeftOuterJoin=students1.join(students2, students1("id")===students2("id"), "left_outer")
  studentsLeftOuterJoin.show(studentsLeftOuterJoin.count.toInt)
  
  //Right outer join
  val studentsRightOuterJoin=students1.join(students2, students1("id")===students2("id"), "right_outer")
  studentsRightOuterJoin.show(studentsRightOuterJoin.count.toInt)
  
  
}