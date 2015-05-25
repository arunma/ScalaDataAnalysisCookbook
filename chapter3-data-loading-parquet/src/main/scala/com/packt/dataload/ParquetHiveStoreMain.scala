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
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext

/**
 * Not proceeding with this recipe because of a bug in Spark due to the fixed by end of this month (May 2015). 
 */

/*
 * 
 * Draft content of this receipe. 

This recipe assumes that Hive is already installed on your machine. Just like most Hadoop subprojects, installation of Hive just involves untarring the hive binary and setting HIVE_HOME and setting PATH.

If this is a fresh installation, let's verify it using hive --version. 


This recipe involves 

1. Setup Parquet support in Hive.  

As we saw in the previous chapter, the Parquet MR project (yes, the same project which has the parquet-tools project) has a whole lot of sub projects that helps making Parquet work with a variety of data models (which we'll see in the next recipe on using Avro Data model with Parquet).  Other than the data models, Parquet plays very well with Hive. This means that while the storage is in Parquet, we could use Hive querying engine to query the data.  Though Spark has Spark SQL for querying, this form of representing Parquet as Hive table would help existing ETL jobs that use Hive (and doesnt run on Spark) to play along well.

Hive has native support for Parquet since 0.13.  Until then, we copy the wbundled jar from the parquet-hive project into the hive/lib. 

The jar that we intend to copy to the HIVE_HOME/lib is parquet-hive-storage-handler-1.6.0rc3.jar.   Not copying this jar would result in an exception. 

FAILED: SemanticException Cannot find class 'parquet.hive.DeprecatedParquetInputFormat'


IMAGE



2. Creative of a Hive table 
Now, let's go to the real stuff. 

Let's first create a Hive Table. 


 

 * 
 */


/**
 create external table studentpq (id STRING, name STRING, phone STRING, email STRING)
  ROW FORMAT SERDE 'parquet.hive.serde.ParquetHiveSerDe'
  STORED AS 
    INPUTFORMAT "parquet.hive.DeprecatedParquetInputFormat"
    OUTPUTFORMAT "parquet.hive.DeprecatedParquetOutputFormat"
    LOCATION 'studentpq';
 * 
 */
object ParquetHiveStoreMain extends App{
  
  val conf = new SparkConf().setAppName("ParquetHiveStore").setMaster("local[2]")

  val sc = new SparkContext(conf)

  val hiveContext = new HiveContext(sc)

  import hiveContext._
  import hiveContext.implicits._
  
  //Convert each line into Student 
  val rddOfStudents = convertCSVToStudents("StudentData.csv", sc)
  
  //Convert RDD[Student] to a Dataframe using sqlContext.implicits
  val studentDFrame = rddOfStudents.toDF()
  
  studentDFrame.printSchema()
  
  //Save DataFrame as Parquet
  studentDFrame.saveAsTable("studentpq")
  
   //The CSV has a header row.  Zipping with index and skipping the first row
  def convertCSVToStudents(filePath: String, sc: SparkContext): RDD[Student] = {
    val rddOfStudents: RDD[Student] = sc.textFile(filePath).zipWithIndex().filter(_._2>0).map(eachLineAndNum => {
      val data=eachLineAndNum._1.split("\\|")
      Student(data(0), data(1), data(2), data(3))
    })
    
    rddOfStudents
  }
  

}

