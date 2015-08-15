organization := "com.packt"

name := "chapter3-data-loading"

scalaVersion := "2.10.4"

val sparkVersion="1.4.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.datastax.spark" %% "spark-cassandra-connector-java" % "1.2.0",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.databricks" %% "spark-csv" % "1.0.3",
  "org.json4s" % "json4s-core_2.10" % "3.2.11",
  "org.json4s" % "json4s-jackson_2.10" % "3.2.11"
  
)

resolvers ++= Seq(
  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

fork := true
