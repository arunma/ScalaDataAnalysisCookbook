package com.packt.scalada.viz.spark

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * This app is just a playing ground before the fnGroupAge actually gets moved into a paragraph in Zeppelin. 
 *  
 */
object ZeppelinUDF extends App {

  def fnGroupAge(age: Int, bucket: Int = 10) = {
    val buckets = Array("0-10", "11-20", "20-30", "31-40", "41-50", "51-60", "61-70", "71-80", "81-90", "91-100", ">100")
    val bucket = buckets((age - 1) / 10)
    bucket
  }

  val conf = new SparkConf().setAppName("csvDataFrame").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val profilesJsonRdd = sqlContext.jsonFile(s"hdfs://localhost:9000/data/scalada/profiles.json")
  val profileDF = profilesJsonRdd.toDF()

  profileDF.printSchema()

  profileDF.show()

  profileDF.registerTempTable("profiles")

  sqlContext.udf.register("fnGroupAge", (age:Long)=>fnGroupAge(age.toInt))

  val dframe = sqlContext.sql("select fnGroupAge(age) from profiles group by fnGroupAge(age)")

  dframe.show()
}