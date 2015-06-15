organization := "com.packt"

name := "chapter4-visualization"

scalaVersion := "2.10.4"
val breezeVersion = "0.11.2"
val sparkVersion="1.3.1"

libraryDependencies  ++= Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,
  "org.scalanlp" %% "breeze-natives" % breezeVersion,
  "org.scalanlp" %% "breeze-viz" % breezeVersion,
  "io.continuum.bokeh" %% "bokeh" % "0.5",
  "joda-time" % "joda-time" % "2.6",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.databricks" %% "spark-csv" % "1.0.3"
)