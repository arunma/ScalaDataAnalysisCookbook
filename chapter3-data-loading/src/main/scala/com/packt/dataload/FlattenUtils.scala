package com.packt.dataload

import scala.reflect.ClassTag

import org.apache.spark.rdd.RDD

object FlattenUtils {
  
  implicit class FlattenOps[V: ClassTag](rdd: RDD[Option[V]]) {
    def flatten: RDD[V] = {
      rdd.flatMap(x => x)
    }
  }

}