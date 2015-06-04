organization := "com.packt"

name := "chapter4-breezevisualization"

scalaVersion := "2.10.4"

val breezeVersion = "0.11.2"

libraryDependencies  ++= Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,
  "org.scalanlp" %% "breeze-natives" % breezeVersion,
  "org.scalanlp" %% "breeze-viz" % breezeVersion,
  "io.continuum.bokeh" %% "bokeh" % "0.5"
)