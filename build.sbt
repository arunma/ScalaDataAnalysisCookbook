organization := "com.packt"

name := "scala-dataanalysis-cookbook"

lazy val root = project.in(file("."))
  .aggregate(chapter1breezeready)
  .aggregate(chapter1sparkcsv)

lazy val chapter1breezeready = project.in( file("chapter1-breeze-gettingready"))
lazy val chapter1sparkcsv = project.in( file("chapter1-spark-csv"))

scalaVersion := "2.10.4"
