organization := "com.packt"

name := "chapter7-goingfurther"

scalaVersion := "2.10.4"
val sparkVersion="1.4.1"

libraryDependencies  ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming-twitter" % sparkVersion,
  "joda-time" % "joda-time" % "2.3",
  "org.elasticsearch" %% "elasticsearch-spark" % "2.1.0",
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
  "com.twitter" %% "chill" % "0.7.0"
)

fork := true
