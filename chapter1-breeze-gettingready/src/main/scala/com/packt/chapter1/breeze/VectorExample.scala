package com.packt.chapter1.breeze

import breeze.linalg.DenseVector
import breeze.linalg.SparseVector

object VectorExample extends App{
  
  val dense=DenseVector(1,2,3,4,5)
  println (dense) //DenseVector(1, 2, 3, 4, 5)
  
  
  val sparse=SparseVector(0.0, 1.0, 0.0, 2.0, 0.0) 
  println (sparse) //SparseVector((0,0.0), (1,1.0), (2,0.0), (3,2.0), (4,0.0))

}

