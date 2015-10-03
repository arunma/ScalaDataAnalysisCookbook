package com.packt.dataload

import org.apache.hadoop.mapreduce.Job
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.parquet.avro.AvroParquetOutputFormat
import org.apache.parquet.avro.AvroWriteSupport
import parquet.hadoop.ParquetOutputFormat
import studentavro.avro.StudentAvro
import parquet.hadoop.ParquetInputFormat
import org.apache.parquet.avro.AvroParquetInputFormat
import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import com.twitter.chill.avro.AvroSerializer
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.Row


object ParquetAvroSchemaMain extends App {

  val conf = new SparkConf().setAppName("AvroModelToParquet").setMaster("local[2]")
  conf.set("spark.kryo.registrator", classOf[StudentAvroRegistrator].getName)
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  
  val job = new Job()

  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)
  sqlContext.setConf("spark.sql.parquet.binaryAsString", "true")
  
  
  val rddOfStudents = convertCSVToStudents("StudentData.csv", sc)

  ParquetOutputFormat.setWriteSupportClass(job, classOf[AvroWriteSupport[StudentAvro]])
  AvroParquetOutputFormat.setSchema(job, StudentAvro.getClassSchema)
  
  val pairRddOfStudentsWithNullKey = rddOfStudents.map(each => (null, each))
  
  pairRddOfStudentsWithNullKey.saveAsNewAPIHadoopFile("studentAvroPq",
    classOf[Void],
    classOf[StudentAvro],
    classOf[AvroParquetOutputFormat[StudentAvro]],
    job.getConfiguration())

  //val avroFrame=sqlContext.parquetFile("hdfs://localhost:9000/scalada/dataloading/studentAvroPq")
  ParquetInputFormat.setReadSupportClass(job, classOf[AvroWriteSupport[StudentAvro]])

  val readStudentsPair = sc.newAPIHadoopFile("studentAvroPq", classOf[AvroParquetInputFormat[StudentAvro]], classOf[Void], classOf[StudentAvro], job.getConfiguration())
  val justStudentRDD: RDD[StudentAvro] = readStudentsPair.map(_._2)
  val studentsAsString = justStudentRDD.collect().take(5).mkString("\n")
  println(studentsAsString)

  //The CSV has a header row.  Zipping with index and skipping the first row
  def convertCSVToStudents(filePath: String, sc: SparkContext): RDD[StudentAvro] = {
    val rddOfStudents: RDD[StudentAvro]=sc.textFile(filePath).flatMap(eachLine => {
      val data = eachLine.split("\\|")
      if (data(0) == "id") None
      else Some(StudentAvro.newBuilder()
        .setId(data(0))
        .setName(data(1))
        .setPhone(data(2))
        .setEmail(data(3)).build())
    })
    rddOfStudents
  }

}

class StudentAvroRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[StudentAvro], AvroSerializer.SpecificRecordBinarySerializer[StudentAvro])
  }
}

