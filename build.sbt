organization := "com.packt"

name := "scala-dataanalysis-cookbook"

lazy val root = project.in(file("."))
  .aggregate(chapter1breezeready)
  .aggregate(chapter1sparkcsv)
  .aggregate(chapter3dataload)
  .aggregate(chapter3dataloadparquet)
  .aggregate(chapter4breezevisualization)
  .aggregate(chapter4sparkvisualization)

lazy val chapter1breezeready = project.in( file("chapter1-breeze-gettingready"))
lazy val chapter1sparkcsv = project.in( file("chapter1-spark-csv"))
lazy val chapter3dataload = project.in( file("chapter3-data-loading"))
lazy val chapter3dataloadparquet = project.in( file("chapter3-data-loading-parquet"))
lazy val chapter4breezevisualization = project.in( file("chapter4-breezevisualization"))
lazy val chapter4sparkvisualization = project.in( file("chapter4-sparkvisualization"))

scalaVersion := "2.10.4"
