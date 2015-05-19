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
	val denseZeros=DenseMatrix.zeros[Double](5,5)
                                                  //> denseZeros  : breeze.linalg.DenseMatrix[Double] = 0.0  0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  0.0  
                                                  //| 0.0  0.0  0.0  0.0  0.0  

	//Sparse Matrix with Zeroes
	val compressedSparseMatrix=CSCMatrix.zeros[Double](5,5)
                                                  //> compressedSparseMatrix  : breeze.linalg.CSCMatrix[Double] = 5 x 5 CSCMatrix
	            
	//Tabulate
	val denseTabulate=DenseMatrix.tabulate[Double](5,5)((firstIdx,secondIdx)=>firstIdx*secondIdx)
                                                  //> denseTabulate  : breeze.linalg.DenseMatrix[Double] = 0.0  0.0  0.0  0.0   0.
                                                  //| 0   
                                                  //| 0.0  1.0  2.0  3.0   4.0   
                                                  //| 0.0  2.0  4.0  6.0   8.0   
                                                  //| 0.0  3.0  6.0  9.0   12.0  
                                                  //| 0.0  4.0  8.0  12.0  16.0  
	
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
                                                  //> randomMatrix  : breeze.linalg.DenseMatrix[Double] = -1.549516696975734   3.6
                                                  //| 28900236479087    -0.12066061512259539  0.9510778211777988   
                                                  //| -2.6478211481748968  -0.6758785217239858  -0.7530887337457499   2.8039893944
                                                  //| 165666   
                                                  //| -0.9619370483066306  -0.6832527540981242  4.016893440285645     0.3352569099
                                                  //| 7698165  
                                                  //| 0.9712624295427421   3.1174181415734803   0.7294633670936055    1.7668681928
                                                  //| 29894    
  
  //Creating from Scala Vector
  val vectFromArray=new DenseMatrix(2,2,Array(2,2,2,2,4,5,6))
                                                  //> vectFromArray  : breeze.linalg.DenseMatrix[Int] = 2  2  
                                                  //| 2  2  
  
  //Matrix Arithmetic
  
  //Matrix element-wise multiplication
	val elementWiseMulti=identityMatrix :* simpleMatrix
                                                  //> elementWiseMulti  : breeze.linalg.DenseMatrix[Int] = 1  0   0   
                                                  //| 0  12  0   
                                                  //| 0  0   23  
	
	//Addition
	val additionMatrix=identityMatrix + simpleMatrix
                                                  //> additionMatrix  : breeze.linalg.DenseMatrix[Int] = 2   2   3   
                                                  //| 11  13  13  
                                                  //| 21  22  24  
                                                  
  
  //Dot product
	val simpleTimesIdentity=simpleMatrix * identityMatrix
                                                  //> dotProduct  : breeze.linalg.DenseMatrix[Int] = 1   2   3   
                                                  //| 11  12  13  
                                                  //| 21  22  23  
                                                  
	//Concatenate a Matrix - Vertically
	val vertConcatMatrix=DenseMatrix.vertcat(identityMatrix, simpleMatrix)
                                                  //> vertConcatMatrix  : breeze.linalg.DenseMatrix[Int] = 1   0   0   
                                                  //| 0   1   0   
                                                  //| 0   0   1   
                                                  //| 1   2   3   
                                                  //| 11  12  13  
                                                  //| 21  22  23  
  val horzConcatMatrix=DenseMatrix.horzcat(identityMatrix, simpleMatrix)
                                                  //> horzConcatMatrix  : breeze.linalg.DenseMatrix[Int] = 1  0  0  1   2   3   
                                                  //| 0  1  0  11  12  13  
                                                  //| 0  0  1  21  22  23  
  
  
	//Other operations - Needs import of breeze.linalg._ and breeze.numerics._
	
	//Max
	val intMaxOfMatrixVals=max (simpleMatrix) //> intMaxOfMatrixVals  : Int = 23
  //Sum
	val intSumOfMatrixVals=sum (simpleMatrix) //> intSumOfMatrixVals  : Int = 108
	
	
	
  //Sqrt
	val sqrtOfMatrixVals= sqrt (simpleMatrix) //> sqrtOfMatrixVals  : breeze.linalg.DenseMatrix[Double] = 1.0               1
                                                  //| .4142135623730951  1.7320508075688772  
                                                  //| 3.3166247903554   3.4641016151377544  3.605551275463989   
                                                  //| 4.58257569495584  4.69041575982343    4.795831523312719   
  //Log
	val log2MatrixVals=log(simpleMatrix)      //> log2MatrixVals  : breeze.linalg.DenseMatrix[Double] = 0.0                 0
                                                  //| .6931471805599453  1.0986122886681098  
                                                  //| 2.3978952727983707  2.4849066497880004  2.5649493574615367  
                                                  //| 3.044522437723423   3.091042453358316   3.1354942159291497  
                                                  
     
  //Convert Matrix of type Int to Matrix of type Double
  val simpleMatrixAsDouble=convert(simpleMatrix, Double)
                                                  //> simpleMatrixAsDouble  : breeze.linalg.DenseMatrix[Double] = 1.0   2.0   3.0
                                                  //|    
                                                  //| 11.0  12.0  13.0  
                                                  //| 21.0  22.0  23.0  
  
  //Calculate Mean and Variance.  Note that this needs a Vector of Double
 	meanAndVariance(simpleMatrixAsDouble)     //> res0: breeze.stats.MeanAndVariance = MeanAndVariance(12.0,75.75,9)
 	
 	//One go
 	meanAndVariance(convert(simpleMatrix, Double))
                                                  //> res1: breeze.stats.MeanAndVariance = MeanAndVariance(12.0,75.75,9)
	 
	 //Mean of the matrix
  mean(simpleMatrixAsDouble)                      //> res2: Double = 12.0
  
  //Standard Deviation
  stddev(simpleMatrixAsDouble)                    //> res3: Double = 8.703447592764606
                                                  
  
  
  //Transpose
	val transposedMatrix=simpleMatrix.t       //> transposedMatrix  : breeze.linalg.DenseMatrix[Int] = 1  11  21  
                                                  //| 2  12  22  
                                                  //| 3  13  23  


	
	
}