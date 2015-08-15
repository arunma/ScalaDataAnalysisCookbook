package com.scalada.goingfurther

import java.util.Date
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.joda.time.DateTimeZone
import org.elasticsearch.spark._
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.api.java.JavaStreamingContext
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.hadoop.cfg.ConfigurationOptions

object TwitterStreaming extends App {

  val config = ConfigFactory.load()

  val consumerKey = config.getString("CONSUMER_KEY")
  val consumerSecret = config.getString("CONSUMER_SECRET")
  val accessToken = config.getString("ACCESS_TOKEN")
  val accessTokenSecret = config.getString("ACCESS_TOKEN_SECRET")

  val conf = new SparkConf()
    .setAppName("TwitterStreaming")
    .setMaster("local[2]")
    .set(ConfigurationOptions.ES_NODES, "localhost") //Default is localhost. Point to ES node when required
    .set(ConfigurationOptions.ES_PORT, "9200")

  val sc = new SparkContext(conf)

  val builder = new ConfigurationBuilder()
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessToken)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .setUseSSL(true)

  val twitterAuth = Some(new OAuthAuthorization(builder.build()))

  val streamingContext = new StreamingContext(sc, Seconds(5))

  val filter = List("fashion", "tech", "startup", "spark")
  val stream = TwitterUtils.createStream(streamingContext, twitterAuth, filter, StorageLevel.MEMORY_AND_DISK)

  stream.map(convertToSimple).foreachRDD { statusRdd =>
    println(statusRdd)
    statusRdd.saveToEs("spark/twstatus")
  }

  val dateTimeZone = DateTimeZone.getDefault

  def convertToSimple(status: twitter4j.Status): SimpleStatus = {
    val hashTags: Array[String] = status.getHashtagEntities().map(eachHT => eachHT.getText())
    val urlArray = if (status.getURLEntities != null) status.getURLEntities().foldLeft((Array[String]()))((r, c) => (r :+ c.getExpandedURL())) else Array[String]()
    val user = status.getUser()
    val utcDate = new Date(dateTimeZone.convertLocalToUTC(status.getCreatedAt.getTime, false))

    SimpleStatus(id = status.getId.toString, content = status.getText(), utcDate,
      hashTags = hashTags, urls = urlArray,
      user = user.getScreenName(), userName = user.getName, userFollowerCount = user.getFollowersCount)
  }

  streamingContext.start()
  streamingContext.awaitTermination()
  
}

case class SimpleStatus(id: String, content: String, date: Date, hashTags: Array[String] = Array[String](),
  urls: Array[String] = Array[String](),
  user: String, userName: String, userFollowerCount: Long)
                        
                        

  
                     