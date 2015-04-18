import breeze.linalg.DenseMatrix
import breeze.linalg._
import breeze.numerics._
import breeze.stats._


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
	val randomMatrix=DenseMatrix.rand(4, 4)   //> randomMatrix  : breeze.linalg.DenseMatrix[Double] = 0.14163307869754793  0.5
                                                  //| 176781656991645   0.8305000748557907    0.7950115771005357   
                                                  //| 0.6014368838589164   0.15382027069903015  0.40695483997582604   0.3698871102
                                                  //| 2312115  
                                                  //| 0.5085383250539783   0.5321984351172919   0.024557404766776703  0.8157394099
                                                  //| 63264    
                                                  //| 0.663374969842732    0.6383926469698742   0.7433916189831493    0.5106795767
                                                  //| 626973   
  
  //Creating from Scala Vector
  val vectFromArray=new DenseMatrix(2,2,Array(2,2,2,2,4,5,6))
                                                  //> vectFromArray  : breeze.linalg.DenseMatrix[Int] = 2  2  
                                                  //| 2  2  
  
  //Matrix Arithmetic
  
  //Matrix element-wise multiplication
	val dotMatrix=identityMatrix :* simpleMatrix
                                                  //> dotMatrix  : breeze.linalg.DenseMatrix[Int] = 1  0   0   
                                                  //| 0  12  0   
                                                  //| 0  0   23  
	
	//Addition
	val additionMatrix=identityMatrix :+ simpleMatrix
                                                  //> additionMatrix  : breeze.linalg.DenseMatrix[Int] = 2   2   3   
                                                  //| 11  13  13  
                                                  //| 21  22  24  
                                                  
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
                                                  
  

}