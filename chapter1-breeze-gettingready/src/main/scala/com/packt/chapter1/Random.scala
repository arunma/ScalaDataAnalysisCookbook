package com.packt.chapter1

import breeze.linalg.DenseVector
import breeze.stats.distributions._
import breeze.linalg._

object Random extends App{
  
  //Uniform distribution with low being 0 and high being 10
  val uniformDist=Uniform(0,10)
  //Samples a single value
  println (uniformDist.sample())
  //Returns a Sequence of the quantity passed in as parameter
  println (uniformDist.sample(2))
  
  //Gaussian distribution with mean being 5 and Standard deviatin being 1
  val gaussianDist=Gaussian(5,1) 
  
  //Poission distribution with mean being 5
  val poissonDist=Poisson(5)
    
  //VECTORS
  //Uniform distribution, Creates the Vector with random values from 0 to 1
  val uniformWithoutParam=DenseVector.rand(10)
  println ("uniformWithoutParam \n"+uniformWithoutParam)
  
  //Creates a Vector of size 10 with uniformly distributed random values with low being 0 and high being 10
  val uniformVectInRange=DenseVector.rand(10, uniformDist)
  println ("uniformVectInRange \n"+uniformVectInRange)     
  
  //Creates a Vector of size 10 with Normally distributed random values with mean being 5 and Standard deviation being 1
  val gaussianVector=DenseVector.rand(10, gaussianDist)
  println ("gaussianVector \n"+gaussianVector)
  
  //Creates a Vector of size 10 with Poisson distribution with mean being 5
  val poissonVector=DenseVector.rand(10, poissonDist)
  println ("poissonVector \n"+poissonVector)
  
  
  //MATRICES
  //Uniform distribution, Creates a 3 * 3 Matrix with random values from 0 to 1
  val uniformMat=DenseMatrix.rand(3, 3)
  println ("uniformMat \n"+uniformMat)
  
  //Creates a 3 * 3 Matrix with uniformly distributed random values with low being 0 and high being 10
  val uniformMatrixInRange=DenseMatrix.rand(3,3, uniformDist)
  println ("uniformMatrixInRange \n"+uniformMatrixInRange)
  
  //Creates a 3 * 3 Matrix with Normally distributed random values with mean being 5 and Standard deviation being 1
  val gaussianMatrix=DenseMatrix.rand(3, 3,gaussianDist)
  println ("gaussianMatrix \n"+gaussianMatrix)
  
  //Creates a 3 * 3 Matrix with Poisson distribution with mean being 5
  val poissonMatrix=DenseMatrix.rand(3, 3,poissonDist)
  println ("poissonMatrix \n"+poissonMatrix)
  
}