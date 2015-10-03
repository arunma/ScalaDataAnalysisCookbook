package com.scalada.goingfurther

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.elasticsearch.spark._
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.SQLContext
import org.elasticsearch.spark.sql._
import java.util.HashMap
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import scala.collection.JavaConverters._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.Seconds
import kafka.serializer.StringDecoder
import java.lang.Double
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.storage.StorageLevel
import org.joda.time.Hours
import org.apache.spark.streaming.Minutes
import kafka.serializer.DefaultDecoder
import org.apache.spark.mllib.classification.StreamingLogisticRegressionWithSGD
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.DataFrame
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.broadcast.Broadcast

/**
 * Reads from ElasticSearch and publishes to Kafka
 *
 * Used to simulate a StreamingLogisticRegression (and in a really round about way)
 *
 */
object LearningSubscriber {

  def main(args: Array[String]) {

    val conf = new SparkConf()
      .setAppName("LearningSubscriber")
      .setMaster("local[2]")

    val sc = new SparkContext(conf)
    val streamingContext = new StreamingContext(sc, Seconds(1))

    val (hashingTf,model) = trainModelFromKafka(streamingContext)
    val predictions=predictTwitterStreamAgainstModel(streamingContext, model, hashingTf)
    //val predictions=predictFileStreamAgainstModel(streamingContext, model, hashingTf)
    
    
    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def trainModelFromKafka(streamingContext: StreamingContext): (Broadcast[HashingTF], StreamingLogisticRegressionWithSGD) = {
    //Subscribe to Kafka stream
    val topics = Set("twtopic")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092", "serializer.class" -> "kafka.serializer.DefaultEncoder")

    /* Regular ZooKeeper method
     * val topicMap = Map("twtopic"->1)
  val zkQuorum = "localhost:2181"
  val consumerGroup="twGroup"
  val kafkaStream=KafkaUtils.createStream(streamingContext, zkQuorum, consumerGroup, topicMap)*/

    val hashingTf = new HashingTF(5000)
    val kafkaStream = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder](streamingContext, kafkaParams, topics).repartition(2)
    val trainingStream = kafkaStream.map {
        case (key, value) =>
          val labeledContent = KryoSerializer.deserialize(value).asInstanceOf[LabeledContent]
          val vector = hashingTf.transform(labeledContent.content)
          LabeledPoint(labeledContent.label, vector)
    }

    //Now that HashingTF is constructed, broadcast HashingTF to be used while prediction by workers
    val broadcastHashingTf=streamingContext.sparkContext.broadcast(hashingTf)

    //Train the model
    val model = new StreamingLogisticRegressionWithSGD()
      .setInitialWeights(Vectors.zeros(5000))
      .setNumIterations(25).setStepSize(0.1).setRegParam(0.001)

    model.trainOn(trainingStream)

    (broadcastHashingTf,model)
  }

  def predictTwitterStreamAgainstModel(streamingContext: StreamingContext, model: StreamingLogisticRegressionWithSGD, broadcastTf:Broadcast[HashingTF]) {
    //Predict on Twitter Stream
    val config = ConfigFactory.load()
    val consumerKey = config.getString("CONSUMER_KEY")
    val consumerSecret = config.getString("CONSUMER_SECRET")
    val accessToken = config.getString("ACCESS_TOKEN")
    val accessTokenSecret = config.getString("ACCESS_TOKEN_SECRET")

    val builder = new ConfigurationBuilder()
      .setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)

    val twitterAuth = Some(new OAuthAuthorization(builder.build()))

    val filter = List("fashion", "tech", "startup", "spark")
    val twitterStream = TwitterUtils.createStream(streamingContext, twitterAuth, filter, StorageLevel.MEMORY_AND_DISK)

    val hashingTf=broadcastTf.value
    
    //Unable to filter for English tweets alone with twitter4j 3.0.3 - filter (status=>status.getLang=="en")
    val contentAndFeatureVector=twitterStream.map { status =>
      val tokens=status.getText().toLowerCase().split(" ")
      val vector=hashingTf.transform(tokens)
      (status.getText(), vector)
    }
    
    val contentAndPrediction=model.predictOnValues(contentAndFeatureVector)
    
    //Not the best way to store the results. Creates a whole lot of files
    contentAndPrediction.saveAsTextFiles("predictions", "txt")

  }
  
}