organization := "com.packt"

name := "chapter6-scalingup"

scalaVersion := "2.10.4"
val sparkVersion="1.4.1"

libraryDependencies  ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "com.databricks" %% "spark-csv" % "1.0.3",
  "org.apache.hadoop"  % "hadoop-client" % "2.6.0",
  ("org.scalanlp" % "epic-parser-en-span_2.10" % "2015.2.19"). 
    exclude("xml-apis", "xml-apis")
)

assemblyJarName in assembly := "scalada-learning-assembly.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case "application.conf"                            => MergeStrategy.concat
  case PathList("org", "cyberneko", "html", xs @ _*) => MergeStrategy.first
  case m if m.toLowerCase.endsWith("manifest.mf")    => MergeStrategy.discard
  case f                                             => (assemblyMergeStrategy in assembly).value(f)
}

