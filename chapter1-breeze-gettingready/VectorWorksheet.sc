import breeze.linalg.DenseVector
import breeze.linalg.SparseVector
import breeze.linalg._
import breeze.numerics._

object VectorWorksheet {
    
  //Dense and Sparse Vector with init values
  val dense=DenseVector(1,2,3,4,5)                //> dense  : breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4, 5)
  val sparse=SparseVector(0.0, 1.0, 0.0, 2.0, 0.0)//> sparse  : breeze.linalg.SparseVector[Double] = SparseVector((0,0.0), (1,1.0)
                                                  //| , (2,0.0), (3,2.0), (4,0.0))
                                                  
                             
	//DenseVector with Zeroes
	val denseZeros=DenseVector.zeros[Double](5)
                                                  //> denseZeros  : breeze.linalg.DenseVector[Double] = DenseVector(0.0, 0.0, 0.0,
                                                  //|  0.0, 0.0)
            
	//Sparse Vector with Zeroes
  val sparseZeros=SparseVector.zeros[Double](5)   //> sparseZeros  : breeze.linalg.SparseVector[Double] = SparseVector()
                                           
  //Tabulate
	val denseTabulate=DenseVector.tabulate[Double](5)(index=>index*index)
                                                  //> denseTabulate  : breeze.linalg.DenseVector[Double] = DenseVector(0.0, 1.0, 4
                                                  //| .0, 9.0, 16.0)
	
	//Range
	val evenNosTill20=DenseVector.range(0, 20, 2)
                                                  //> evenNosTill20  : breeze.linalg.DenseVector[Int] = DenseVector(0, 2, 4, 6, 8,
                                                  //|  10, 12, 14, 16, 18)
                                                  
	//Fill
	val denseJust2s=DenseVector.fill(10, 2)   //> denseJust2s  : breeze.linalg.DenseVector[Int] = DenseVector(2, 2, 2, 2, 2, 2
                                                  //| , 2, 2, 2, 2)
  
  //Slice
  val fourThroughSevenIndexVector= evenNosTill20.slice(4, 7, 1)
                                                  //> fourThroughSevenIndexVector  : breeze.linalg.DenseVector[Int] = DenseVector(
                                                  //| 8, 10, 12)
  //Creating from Scala Vector
  val vectFromArray=DenseVector(collection.immutable.Vector(1,2,3,4))
                                                  //> vectFromArray  : breeze.linalg.DenseVector[scala.collection.immutable.Vector
                                                  //| [Int]] = DenseVector(Vector(1, 2, 3, 4))
  
  //Vector Arithmetic
  
  //Dot product
	val dotVector=evenNosTill20.dot(denseJust2s)
                                                  //> dotVector  : Int = 180
	
	//Addition
	val additionVector=evenNosTill20 + denseJust2s
                                                  //> additionVector  : breeze.linalg.DenseVector[Int] = DenseVector(2, 4, 6, 8, 
                                                  //| 10, 12, 14, 16, 18, 20)
                                                  
	//in place addition of values
	val inPlaceValueAddition=evenNosTill20 :+2//> inPlaceValueAddition  : breeze.linalg.DenseVector[Int] = DenseVector(2, 4, 
                                                  //| 6, 8, 10, 12, 14, 16, 18, 20)
	//Concatenate a Vector
	
	val concatVector=DenseVector.vertcat(evenNosTill20, denseJust2s)
                                                  //> concatVector  : breeze.linalg.DenseVector[Int] = DenseVector(0, 2, 4, 6, 8,
                                                  //|  10, 12, 14, 16, 18, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2)
                       
	//Other operations - Needs import of breeze.linalg._
	
	//Max
	val intMaxOfVectorVals=max (evenNosTill20)//> intMaxOfVectorVals  : Int = 18
  //Sum
	val intSumOfVectorVals=sum (evenNosTill20)//> intSumOfVectorVals  : Int = 90
  //Sqrt
	val sqrtOfVectorVals= sqrt (evenNosTill20)//> sqrtOfVectorVals  : breeze.linalg.DenseVector[Double] = DenseVector(0.0, 1.
                                                  //| 4142135623730951, 2.0, 2.449489742783178, 2.8284271247461903, 3.16227766016
                                                  //| 83795, 3.4641016151377544, 3.7416573867739413, 4.0, 4.242640687119285)
  //Log
	val log2VectorVals=log(evenNosTill20)     //> log2VectorVals  : breeze.linalg.DenseVector[Double] = DenseVector(-Infinity
                                                  //| , 0.6931471805599453, 1.3862943611198906, 1.791759469228055, 2.079441541679
                                                  //| 8357, 2.302585092994046, 2.4849066497880004, 2.6390573296152584, 2.77258872
                                                  //| 2239781, 2.8903717578961645)
}