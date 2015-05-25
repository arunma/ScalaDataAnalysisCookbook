package com.packt.dataload

import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.packt.dataload.model.caseclass.Student
import scala.io.Source
import org.apache.spark.SparkContext._
import com.packt.dataload.model.caseclass.Student
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SQLConf

object ParquetCaseClassMain extends App{
  
  val conf = new SparkConf().setAppName("CaseClassToParquet").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)
  
  //Treat binary encoded values as Strings
  sqlContext.setConf("spark.sql.parquet.binaryAsString","true")
  sqlContext.setConf("spark.sql.parquet.compression.codec", "snappy")
  //sqlContext.setConf("spark.sql.parquet.compression.codec", "gzip")
  //sqlContext.setConf("spark.sql.parquet.compression.codec", "lzo")
  

  import sqlContext.implicits._
  
  //Convert each line into Student 
  val rddOfStudents = convertCSVToStudents("StudentData.csv", sc)
  
  //Convert RDD[Student] to a Dataframe using sqlContext.implicits
  val studentDFrame = rddOfStudents.toDF()
  
  //Save DataFrame as Parquet
  //studentDFrame.saveAsParquetFile("studentPq.parquet")
  studentDFrame.save("studentPq.parquet", "parquet", SaveMode.Overwrite)
  
  //Read data for confirmation
  val pqDFrame=sqlContext.parquetFile("studentPq.parquet")
  pqDFrame.show()
  
  
   //The CSV has a header row.  Zipping with index and skipping the first row
  def convertCSVToStudents(filePath: String, sc: SparkContext): RDD[Student] = {
    val rddOfStudents: RDD[Student] = sc.textFile(filePath).zipWithIndex().filter(_._2>0).map(eachLineAndNum => {
      val data=eachLineAndNum._1.split("\\|")
      Student(data(0), data(1), data(2), data(3))
    })
    
    rddOfStudents
  }

}

