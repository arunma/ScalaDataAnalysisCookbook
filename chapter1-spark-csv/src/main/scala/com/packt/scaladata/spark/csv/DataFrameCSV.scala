package com.packt.scaladata.spark.csv

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv._
import scala.collection.JavaConverters._
import org.apache.spark.sql.SaveMode


object DataFrameCSV extends App{
  
  //The SparkConf holds all the information for running this Spark 'cluster'   
  //For this recipe, we are running locally. And we intend to use just 2 cores in the machine - local[2]
  val conf = new SparkConf().setAppName("csvDataFrame").setMaster("local[2]")
  
  //Initialize Spark context with Spark configuration.  This is the core entry point to do anything with Spark
  val sc = new SparkContext(conf)
  
  //The easiest way to query data in Spark is to use SQL queries. In fact, that's the recommended way
  val sqlContext=new SQLContext(sc)
  
  //Now, lets load our pipe-separated file
  //Student is of type org.apache.spark.sql.DataFrame
  val students=sqlContext.csvFile(filePath="StudentData.csv", useHeader=true, delimiter='|')
  
  //Print the schema of this input
  students.printSchema
  
  //Sample the first 5 records
  students.take(5).foreach(println)
  
   //Sample n records along with headers 
  students.show (3)  
  
  //Sample just the 'email' field
  students.select("email").take(5).foreach(println)
  
  //Sample selected columns
  students.select("studentName", "email").show() //Defaults to 20 rows
 
  //Print the first 5 records that has student id more than 5
  students.filter("id > 5").take(5).foreach(println)
  
  //Records with No student names
  students.filter("studentName =''").foreach(println)
  
  //Show all records whose student names are empty or null
  students.filter("studentName ='' OR studentName = 'NULL'").foreach(println)
  
  //Get all students whose name starts with the letter 'M'
  students.filter("SUBSTR(studentName,0,1) ='M'").foreach(println)
  
  //Order by descending order
  students.sort(students("studentName").desc).show(10)
  
  //Order by a list of column names
  students.sort("studentName", "id").show(10)
  
  val options=Map("header"->"true", "path"->"ModifiedStudent.csv")
  
  //Modify dataframe - pick studentname and email columns, change 'studentName' column name to just 'name' 
  val copyOfStudents=students.select(students("studentName").as("name"), students("email"))
  //Save this new dataframe with headers and with file name "ModifiedStudent.csv"
  copyOfStudents.save("com.databricks.spark.csv", SaveMode.Overwrite, options)
  
  //Load the saved data and verify the schema and list some records
  //Instead of using the csvFile, you could do a 'load' 
  val newStudents=sqlContext.load("com.databricks.spark.csv",options)
  newStudents.printSchema()
  println ("new Students")
  newStudents.show()
  
  
}