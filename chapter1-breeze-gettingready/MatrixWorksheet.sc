import breeze.linalg.DenseMatrix
import breeze.linalg._
import breeze.numerics._
import breeze.stats._
import breeze.stats.distributions._


object MatrixWorksheet {
    
  //Dense and Sparse Matrix with init values
  val simpleMatrix=DenseMatrix((1,2,3),(11,12,13),(21,22,23))
                                                  //> simpleMatrix  : breeze.linalg.DenseMatrix[Int] = 1   2   3   
                                                  //| 11  12  13  
                                                  //| 21  22  23  
  
  val sparseMatrix=CSCMatrix((1,0,0),(11,0,0),(0,0,23))
                                                  //> sparseMatrix  : breeze.linalg.CSCMatrix[Int] = 3 x 3 CSCMatrix
                                                  //| (0,0) 1
                                                  //| (1,0) 11
                                                  //| (2,2) 23
                             
	//DenseMatrix with Zeroes
	val denseZeros=DenseMatrix.zeros[Double](5,4)
                                                  //> denseZeros  : breeze.linalg.DenseMatrix[Double] = 0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  

	//Sparse Matrix with Zeroes
	val compressedSparseMatrix=CSCMatrix.zeros[Double](5,4)
                                                  //> compressedSparseMatrix  : breeze.linalg.CSCMatrix[Double] = 5 x 4 CSCMatrix
	            
	//Tabulate
	val denseTabulate=DenseMatrix.tabulate(5,4)((firstIdx,secondIdx)=>firstIdx*secondIdx)
                                                  //> denseTabulate  : breeze.linalg.DenseMatrix[Int] = 0  0  0  0   
                                                  //| 0  1  2  3   
                                                  //| 0  2  4  6   
                                                  //| 0  3  6  9   
                                                  //| 0  4  8  12  
	
	//Linearly spaced Vector
	val spaceVector=breeze.linalg.linspace(2, 10, 5)
                                                  //> spaceVector  : breeze.linalg.DenseVector[Double] = DenseVector(2.0, 4.0, 6.0
                                                  //| , 8.0, 10.0)
	
	//Identity matrix
	val identityMatrix=DenseMatrix.eye[Int](3)//> identityMatrix  : breeze.linalg.DenseMatrix[Int] = 1  0  0  
                                                  //| 0  1  0  
                                                  //| 0  0  1  
                                                  
	//Fill
	val randomMatrix=DenseMatrix.rand(4, 4, Gaussian.distribution(0,5.0))
                                                  //> randomMatrix  : breeze.linalg.DenseMatrix[Double] = 4.877125989887656     4.
                                                  //| 744961513079461    -5.975433443984735   1.4965595529016518   
                                                  //| 1.4220859446091827    -0.5629808813310618  3.9628481804424283   -0.789496116
                                                  //| 1265082  
                                                  //| 1.4431061334413189    -0.8635804900538949  1.7891718302968342   1.4044210064
                                                  //| 570106   
                                                  //| -0.38627531878763116  0.8376526017090755   -4.1572911233970355  -0.597863066
                                                  //| 6858334  
  
  //Creating from Scala Vector
  val vectFromArray=new DenseMatrix(2,2,Array(2,3,4,5,6,7))
                                                  //> vectFromArray  : breeze.linalg.DenseMatrix[Int] = 2  4  
                                                  //| 3  5  
  
  val vectFromArrayIobe=new DenseMatrix(2,2,Array(2,3,4))
                                                  //> java.lang.ArrayIndexOutOfBoundsException: 3
                                                  //| 	at breeze.linalg.DenseMatrix$mcI$sp.apply$mcI$sp(DenseMatrix.scala:87)
                                                  //| 	at breeze.linalg.DenseMatrix$mcI$sp.apply(DenseMatrix.scala:82)
                                                  //| 	at breeze.linalg.DenseMatrix$mcI$sp.apply(DenseMatrix.scala:53)
                                                  //| 	at breeze.linalg.Matrix$$anonfun$colWidth$1$1.apply$mcII$sp(Matrix.scala
                                                  //| :71)
                                                  //| 	at breeze.linalg.Matrix$$anonfun$colWidth$1$1.apply(Matrix.scala:71)
                                                  //| 	at breeze.linalg.Matrix$$anonfun$colWidth$1$1.apply(Matrix.scala:71)
                                                  //| 	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike
                                                  //| .scala:244)
                                                  //| 	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike
                                                  //| .scala:244)
                                                  //| 	at scala.collection.immutable.Range.foreach(Range.scala:141)
                                                  //| 	at scala.collection.TraversableLike$class.map(TraversableLike.scala:244)
                                                  //| 
                                                  //| 	at scala.collection.AbstractTraversable.map(Traversable.scala:105)
                                                  //| 	at breeze.linalg.Matrix$class.colWidth$1(Matrix.scala:71)
                                                  //| 	at breez
                                                  //| Output exceeds cutoff limit.
                                                  
  //Matrix Arithmetic
  
  //Matrix element-wise multiplication
	val elementWiseMulti=identityMatrix :* simpleMatrix
	
	//Addition
	val additionMatrix=identityMatrix + simpleMatrix
                                                  
  
  //Dot product
	val simpleTimesIdentity=simpleMatrix * identityMatrix
                                                  
	//Concatenate a Matrix - Vertically
	val vertConcatMatrix=DenseMatrix.vertcat(identityMatrix, simpleMatrix)
  val horzConcatMatrix=DenseMatrix.horzcat(identityMatrix, simpleMatrix)
  
  
	//Other operations - Needs import of breeze.linalg._ and breeze.numerics._
	
	//Max
	val intMaxOfMatrixVals=max (simpleMatrix)
  //Sum
	val intSumOfMatrixVals=sum (simpleMatrix)
	
	
	
  //Sqrt
	val sqrtOfMatrixVals= sqrt (simpleMatrix)
  //Log
	val log2MatrixVals=log(simpleMatrix)
                                                  
     
  //Convert Matrix of type Int to Matrix of type Double
  val simpleMatrixAsDouble=convert(simpleMatrix, Double)
  
  //Calculate Mean and Variance.  Note that this needs a Vector of Double
 	meanAndVariance(simpleMatrixAsDouble)
 	
 	//One go
 	meanAndVariance(convert(simpleMatrix, Double))
	 
	 //Mean of the matrix
  mean(simpleMatrixAsDouble)
  
  //Standard Deviation
  stddev(simpleMatrixAsDouble)
                                                  
  
  
  //Transpose
	val transposedMatrix=simpleMatrix.t


	
	
}