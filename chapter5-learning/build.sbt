organization := "com.packt"

name := "chapter4-learning"

scalaVersion := "2.10.4"
val sparkVersion="1.4.1"

libraryDependencies  ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.databricks" %% "spark-csv" % "1.0.3",
  "org.scalanlp" % "epic_2.10" % "0.3.1",
  "org.scalanlp" % "epic-parser-en-span_2.10" % "2015.2.19",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1" classifier "models"
)

fork := true