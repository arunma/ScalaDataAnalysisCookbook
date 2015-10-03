package com.scalada.goingfurther

import scala.collection.JavaConverters.mapAsJavaMapConverter
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.elasticsearch.spark.sql.sqlContextFunctions
import com.twitter.chill.ScalaKryoInstantiator
import kafka.serializer.Decoder
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties

/**
 * Reads from ElasticSearch and publishes to Kafka
 *
 * Used to simulate a StreamingLogisticRegression (and in a really round about way)
 *
 */
object KafkaStreamProducerFromES {

  def main(args: Array[String]) {

    val conf = new SparkConf()
      .setAppName("KafkaStreamProducerFromES")
      .setMaster("local[2]")
      .set(ConfigurationOptions.ES_NODES, "localhost") //Default is localhost. Point to ES node when required
      .set(ConfigurationOptions.ES_PORT, "9200")
      .set("spark.driver.allowMultipleContexts", "true")

    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val twitterStatusDf = convertElasticSearchDataToDataFrame(sqlContext)
    val labeledPointRdd = convertToLabeledContentRdd(twitterStatusDf)
    publishToKafka(labeledPointRdd)
    
  }

  def convertElasticSearchDataToDataFrame(sqlContext: SQLContext) = {
    val twStatusDf = sqlContext.esDF("spark/twstatus")
    twStatusDf.show()
    twStatusDf
  }

  def convertToLabeledContentRdd(twStatusDf: DataFrame) = {
    //Convert the content alone to a (label, content) pair 
    val labeledPointRdd = twStatusDf.map{row =>
        val content = row.getAs[String]("content").toLowerCase()
        val tokens = content.split(" ") //A very primitive space based tokenizer
        val labeledContent=if (content.contains("fashion")) LabeledContent(1, tokens)
        else LabeledContent(0, tokens)
        println (labeledContent.label, content)
        labeledContent
    }
    labeledPointRdd
  }

  def publishToKafka(labeledPointRdd: RDD[LabeledContent]) {
    labeledPointRdd.foreachPartition { iterator =>

      val properties = Map[String, Object](ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092", "serializer.class" -> "kafka.serializer.DefaultEncoder").asJava
      val producer = new KafkaProducer[String, Array[Byte]](properties, new StringSerializer, new ByteArraySerializer)

      iterator.foreach { lContent =>
        val serializedPoint = KryoSerializer.serialize(lContent)
        producer.send(new ProducerRecord[String, Array[Byte]]("twtopic", serializedPoint))
      }
    }
  }

}

case class LabeledContent(label: Double, content: Array[String])

object KryoSerializer {
  private val kryoPool = ScalaKryoInstantiator.defaultPool

  def serialize[T](anObject: T): Array[Byte] = kryoPool.toBytesWithClass(anObject)
  def deserialize[T](bytes: Array[Byte]): T = kryoPool.fromBytes(bytes).asInstanceOf[T]
}