package com.packt.scalada.learning

import java.util.Properties
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.io.Source
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.optimization.Updater
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.GeneralizedLinearModel
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.mllib.optimization.SquaredL2Updater

object BinaryClassificationSpam extends App {

  val conf = new SparkConf().setAppName("binaryClassificationSpam").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  //Frankly, we could make this a tuple but this looks neat
  case class Document(label: String, content: String)

  val docs = sc.textFile("SMSSpamCollection").map(line => {
    val words = line.split("\t")
    Document(words.head.trim(), words.tail.mkString(" "))
  })

  //Use Scala NLP - Epic
  /*docs.mapPartitions{ docIter=>

    val segmenter=MLSentenceSegmenter.bundled().get
    val tokenizer = new TreebankTokenizer()
    
    docIter.map{doc=>
    	val sentences=segmenter.apply(doc.content)
    	sentences.map(sentence=> tokenizer(sentence))
    }
  }*/

  def corePipeline(): StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    new StanfordCoreNLP(props)
  }

  def lemmatize(nlp: StanfordCoreNLP, content: String): List[String] = {
    //We are required to prepare the text as 'annotatable' before we annotate :-)
    val document = new Annotation(content)

    //Annotate
    nlp.annotate(document)

    //Extract all sentences
    val sentences = document.get(classOf[SentencesAnnotation]).asScala

    //Extract lemmas from sentences
    val lemmas = sentences.flatMap { sentence =>
      val tokens = sentence.get(classOf[TokensAnnotation]).asScala
      tokens.map(token => token.getString(classOf[LemmaAnnotation]))

    }

    //Only lemmas with letters or digits will be considered. Also consider only those words which has a length of at least 2
    lemmas.toList.filter(lemma => lemma.forall(_.isLetterOrDigit)).filter(_.length() > 1)

  }

  val labeledPointsRdd: RDD[LabeledPoint] = docs.mapPartitions { docIter =>
    val corenlp = corePipeline()
    val stopwords = Source.fromFile("stopwords.txt").getLines()
    val hashingTf = new HashingTF(5000)

    docIter.map { doc =>
      val lemmas = lemmatize(corenlp, doc.content)
      //remove all the stopwords from the lemma list
      lemmas.filterNot(lemma => stopwords.contains(lemma))

      //Generates a term frequency vector from the features
      val features = hashingTf.transform(lemmas)

      //example : List(until, jurong, point, crazy, available, only, in, bugi, great, world, la, buffet, Cine, there, get, amore, wat)
      new LabeledPoint(
        if (doc.label.equals("ham")) 0 else 1,
        features)

    }
  }.cache()

  def getAlgorithm(algo: String, iterations: Int, stepSize: Double, regParam: Double) = algo match {
    case "logsgd" => {
      val algo = new LogisticRegressionWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(regParam)
      algo
    }
    case "logbfgs" => {
      val algo = new LogisticRegressionWithLBFGS()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setRegParam(regParam)
      algo
    }
    case "svm" => {
      val algo = new SVMWithSGD()
      algo.setIntercept(true).optimizer.setNumIterations(iterations).setStepSize(stepSize).setRegParam(regParam)
      algo
    }
  }

  //Split dataset
  val spamPoints = labeledPointsRdd.filter(point => point.label == 1).randomSplit(Array(0.8, 0.2))
  val hamPoints = labeledPointsRdd.filter(point => point.label == 0).randomSplit(Array(0.8, 0.2))
  
  println ("Spam count:"+(spamPoints(0).count)+"::"+(spamPoints(1).count))
  println ("Ham count:"+(hamPoints(0).count)+"::"+(hamPoints(1).count))

  val trainingSpamSplit = spamPoints(0)
  val testSpamSplit = spamPoints(1)

  val trainingHamSplit = hamPoints(0)
  val testHamSplit = hamPoints(1)

  val trainingSplit = trainingSpamSplit ++ trainingHamSplit
  val testSplit = testSpamSplit ++ testHamSplit

  val logisticWithSGD = getAlgorithm("logsgd", 100, 1, 0.001)
  val logisticWithBfgs = getAlgorithm("logbfgs", 100, 1, 0.001)
  val svmWithSGD = getAlgorithm("svm", 100, 1, 0.001)

  //Calculate evaluation metrics
  //calculateMetrics(runClassification(logisticWithSGD, trainingSplit, testSplit), "Logistic Regression with SGD")
  //calculateMetrics(runClassification(logisticWithBfgs, trainingSplit, testSplit), "Logistic Regression with BFGS")
  calculateMetrics(runClassification(svmWithSGD, trainingSplit, testSplit), "SVM with SGD")
  
  
  def runClassification(algorithm: GeneralizedLinearAlgorithm[_ <: GeneralizedLinearModel], trainingData:RDD[LabeledPoint], testData:RDD[LabeledPoint]): RDD[(Double, Double)] = {
    val model = algorithm.run(trainingData)
    val predicted = model.predict(testSplit.map(point => point.features))
    val actuals = testData.map(point => point.label)
    val predictsAndActuals: RDD[(Double, Double)] = predicted.zip(actuals)
    predictsAndActuals
  }

  def calculateMetrics(predictsAndActuals: RDD[(Double, Double)], algorithm: String) {

    val accuracy = 1.0*predictsAndActuals.filter(predActs => predActs._1 == predActs._2).count() / predictsAndActuals.count()
    val binMetrics = new BinaryClassificationMetrics(predictsAndActuals)
    println(s"************** Printing metrics for $algorithm ***************")
    println(s"Area under ROC ${binMetrics.areaUnderROC}")
    //println(s"Accuracy $accuracy")
    //binMetrics.fMeasureByThreshold.foreach(thresholdF1 => println(s"Threshold and F1s ${thresholdF1._1} :::: ${thresholdF1._2}"))
    
    val metrics = new MulticlassMetrics(predictsAndActuals)
    println(s"Precision : ${metrics.precision}")
    println(s"Confusion Matrix \n${metrics.confusionMatrix}")
    println(s"************** ending metrics for $algorithm *****************")
    
  }

}