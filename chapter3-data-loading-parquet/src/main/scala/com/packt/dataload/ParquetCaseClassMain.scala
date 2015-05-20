package com.packt.dataload

import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.packt.dataload.model.caseclass.Student
import scala.io.Source
import org.apache.spark.SparkContext._
import com.packt.dataload.model.caseclass.Student

object ParquetCaseClassMain extends App{
  
  val conf = new SparkConf().setAppName("CaseClassToParquet").setMaster("local[1]")

  val sc = new SparkContext(conf)

  val sqlContext = new SQLContext(sc)
  sqlContext.setConf("spark.sql.parquet.binaryAsString","true")

  //The CSV has a header row.  Zipping with index and skipping the first row
  def convertCSVToStudents(filePath: String, sc: SparkContext): RDD[Student] = {
    val rddOfStudents: RDD[Student] = sc.textFile(filePath).zipWithIndex().filter(_._2>0).map(eachLineAndNum => {
      val data=eachLineAndNum._1.split("\\|")
      Student(data(0), data(1), data(2), data(3))
    })
    
    rddOfStudents
  }

  val rddOfStudents = convertCSVToStudents("StudentData.csv", sc)
  
  //Optionally convert Students Case classes into Dataframe and then save it
  val studentDFrame = sqlContext.createDataFrame(rddOfStudents)
  //Save DataFrame as Parquet
  studentDFrame.saveAsParquetFile("studentPq.parquet")
  
  
  //Read data for confirmation
  val pqDFrame=sqlContext.parquetFile("studentPq.parquet")
  
  pqDFrame.foreach(println)

}

