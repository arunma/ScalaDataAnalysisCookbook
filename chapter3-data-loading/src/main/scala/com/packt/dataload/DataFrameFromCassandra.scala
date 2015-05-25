package com.packt.dataload

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv._
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql.CassandraConnector
import scala.io.Source

/**
 * 
 * Stalling this recipe because this wont work on Spark 1.3.0 because cassandra spark connector isnt available for 1.3.0 yet.
 * (May 22, 2015)
 */
object DataFrameFromCassandra extends App {

  val conf = new SparkConf().setAppName("DataFromCassandra").setMaster("local[2]")
  conf.set ("spark.cassandra.connection.host", "localhost")
  
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)
  
  val students=Source.fromFile("StudentData.csv").getLines()
  
  CassandraConnector(conf).withSessionDo{ session=>
	  students.foreach{ eachLine =>
	    val params=eachLine.split("\\|")
	    //println (params.mkString(","))
	    session.execute(s"insert into scalada.student (id, name, phone, email) values ('${params(0)}', '${params(1)}', '${params(2)}', '${params(2)}')")
	  }
  }
  
  val cassandraRDD=sc.cassandraTable("scalada", "student")
  
  println (cassandraRDD.collect().take(5).mkString("\n"))
  
  //Transform name to upper case and then save it back.  Or optionally, we could skip the INSERT statement above and replace it by calling the saveAsCassandraTable
  //on the rdd in the DataFrame  
  cassandraRDD.saveAsCassandraTable("scalada", "student")
  
}