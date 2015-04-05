organization := "com.packt"

name := "chapter1-breeze"

scalaVersion := "2.11.3"

libraryDependencies  ++= Seq(
  "org.scalanlp" %% "breeze" % "0.11.2",
  //Optional - the 'why' is explained in the How it works section
  "org.scalanlp" %% "breeze-natives" % "0.11.2"
)