organization := "com.packt"

name := "scala-dataanalysis-cookbook"

lazy val root = project.in(file("."))
  .aggregate(chapter1breezeready)

lazy val chapter1breezeready = project.in( file("chapter1-breeze-gettingready"))

scalaVersion := "2.10.4"
