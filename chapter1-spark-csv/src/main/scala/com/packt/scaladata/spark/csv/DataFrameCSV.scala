package com.packt.scaladata.spark.csv

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv._
import scala.collection.JavaConverters._
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame


object DataFrameCSV extends App{
  
  //The SparkConf holds all the information for running this Spark 'cluster'   
  //For this recipe, we are running locally. And we intend to use just 2 cores in the machine - local[2]
  val conf = new SparkConf().setAppName("csvDataFrame").setMaster("local[2]")
  
  //Initialize Spark context with Spark configuration.  This is the core entry point to do anything with Spark
  val sc = new SparkContext(conf)
  
  //The easiest way to query data in Spark is to use SQL queries. In fact, that's the recommended way
  val sqlContext=new SQLContext(sc)
  
  //Now, lets load our pipe-separated file
  //students is of type org.apache.spark.sql.DataFrame
  val students=sqlContext.csvFile(filePath="StudentData.csv", useHeader=true, delimiter='|')
  
  //Print the schema of this input
  students.printSchema
  
   //Sample n records along with headers 
  students.show (3)
  
  //Sample 20 records along with headers 
  students.show ()  
  
  //Sample the first 5 records
  students.head(5).foreach(println)
  
  //Alias of head
  students.take(5).foreach(println)
    
  //Select just the email id to a different dataframe
  val emailDataFrame:DataFrame=students.select("email")
  
  emailDataFrame.show(3)
  
  //Select more than one column and create a different dataframe
  val studentEmailDF=students.select("studentName", "email")
  
  studentEmailDF.show(3)
  
  //Print the first 5 records that has student id more than 5
  students.filter("id > 5").show(7)
  
  //Records with No student names
  students.filter("studentName =''").show(7)
  
  //Show all records whose student names are empty or null
  students.filter("studentName ='' OR studentName = 'NULL'").show(7)
  
  //Get all students whose name starts with the letter 'M'
  students.filter("SUBSTR(studentName,0,1) ='M'").show(7)
  
  //The real power of DataFrames lies in the way we could treat it like a relational table and use SQL to query
  //Step 1. Register the students dataframe as a table with name "students" (or any name)
  students.registerTempTable("students")
  
  //Step 2. Query it away
  val dfFilteredBySQL=sqlContext.sql("select * from students where studentName!='' order by email desc")
  
  dfFilteredBySQL.show(7)
  
  //You could also optionally order the dataframe by column without registering it as a table.
  //Order by descending order
  students.sort(students("studentName").desc).show(10)
  
  //Order by a list of column names - without using SQL
  students.sort("studentName", "id").show(10)
  
  
  //Now, let's save the modified dataframe with a new name
  val options=Map("header"->"true", "path"->"ModifiedStudent.csv")
  
  //Modify dataframe - pick studentname and email columns, change 'studentName' column name to just 'name' 
  val copyOfStudents=students.select(students("studentName").as("name"), students("email"))
  
  copyOfStudents.show()
  //Save this new dataframe with headers and with file name "ModifiedStudent.csv"
  copyOfStudents.save("com.databricks.spark.csv", SaveMode.Overwrite, options)
  
  //Load the saved data and verify the schema and list some records
  //Instead of using the csvFile, you could do a 'load' 
  val newStudents=sqlContext.load("com.databricks.spark.csv",options)
  newStudents.printSchema()
  println ("new Students")
  newStudents.show()
  
}