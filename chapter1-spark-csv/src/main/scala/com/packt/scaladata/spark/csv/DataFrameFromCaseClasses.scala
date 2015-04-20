package com.packt.scaladata.spark.csv

import scala.reflect.runtime.universe

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext


case class Employee(id:Int, name:String)

object DataFrameFromCaseClasses extends App {
  
  //The SparkConf holds all the information for running this Spark 'cluster'   
  //For this recipe, we are running locally. And we intend to use just 2 cores in the machine - local[2]
  val conf = new SparkConf().setAppName("colRowDataFrame").setMaster("local[2]")
  
  //Initialize Spark context with Spark configuration.  This is the core entry point to do anything with Spark
  val sc = new SparkContext(conf)
  
  //The easiest way to query data in Spark is to use SQL queries. In fact, that's the recommended way
  val sqlContext=new SQLContext(sc)
  
  val listOfEmployees =List(Employee(1,"Arun"), Employee(2, "Jason"), Employee (3, "Abhi"))
  
  //Pass in the Employees into the `createDataFrame` function. 
  val empFrame=sqlContext.createDataFrame(listOfEmployees)
  
  empFrame.printSchema
  
  empFrame.show(3)
  
  //If you would like to have a different name for the data frame other than the names specified in the Case class,
  //use the withColumnRenamed function
  
  val empFrameWithRenamedColumns=sqlContext.createDataFrame(listOfEmployees).withColumnRenamed("id", "empId")
  
  empFrameWithRenamedColumns.printSchema
  //Watch out for the columns with a "." in them. This is an open bug and needs to be fixed as of 1.3.0
  
  empFrameWithRenamedColumns.registerTempTable("employeeTable")
  
  val sortedByNameEmployees=sqlContext.sql("select * from employeeTable order by name desc")
  
  sortedByNameEmployees.show()
  
  //Create dataframe from Tuple. Of course, you could rename the column using the withColumnRenamed
  val empFrame1=sqlContext.createDataFrame(Seq((1,"Android"), (2, "iPhone")))
  empFrame1.printSchema
  empFrame1.show()
  
}

