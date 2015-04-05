import breeze.linalg.DenseMatrix
import breeze.linalg._
import breeze.numerics._


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
	
	//Identity matrix
	val identityMatrix=DenseMatrix.eye[Int](3)//> identityMatrix  : breeze.linalg.DenseMatrix[Int] = 1  0  0  
                                                  //| 0  1  0  
                                                  //| 0  0  1  
                                                  
	//Fill
	val randomMatrix=DenseMatrix.rand(4, 4)   //> randomMatrix  : breeze.linalg.DenseMatrix[Double] = 0.09762565779429777   0.
                                                  //| 01089176285376725  0.2660579009292807   0.19428193961985674  
                                                  //| 0.9662568115400412    0.718377391997945    0.8230367668470933   0.3957540854
                                                  //| 393169   
                                                  //| 0.9080090988364429    0.7697780247035393   0.49887760321635066  0.2672201910
                                                  //| 5654415  
                                                  //| 3.326843165250004E-4  0.447925644082819    0.8195838733418965   0.7682752255
                                                  //| 172411   
  
  //Creating from Scala Vector
  val vectFromArray=new DenseMatrix(2,2,Array(2,2,2,2))
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

}