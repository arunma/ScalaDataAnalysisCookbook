package com.packt.dataload

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.io.Source
import com.databricks.spark.csv._
import java.sql.DriverManager
import com.typesafe.config.ConfigFactory

object LoadDataIntoMySQL extends App {

  val conf = new SparkConf().setAppName("LoadDataIntoMySQL").setMaster("local[2]")
  val config=ConfigFactory.load()
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val students = sqlContext.csvFile(filePath = "StudentData.csv", useHeader = true, delimiter = '|')

  students.foreachPartition { iter=>
      val conn = DriverManager.getConnection(config.getString("mysql.connection.url"))
      val statement = conn.prepareStatement("insert into scalada.student (id, name, phone, email) values (?,?,?,?) ")
      
      for (eachRow <- iter) {
        statement.setString(1, eachRow.getString(0))
        statement.setString(2, eachRow.getString(1))
        statement.setString(3, eachRow.getString(2))
        statement.setString(4, eachRow.getString(3))
        statement.addBatch()
      }
      
      statement.executeBatch()
      conn.close()
      println ("All rows inserted successfully")
  }

}