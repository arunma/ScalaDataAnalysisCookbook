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

	val doubleRange=DenseVector.rangeD(0, 5, 0.5)
                                                  //> doubleRange  : breeze.linalg.DenseVector[Double] = DenseVector(0.0, 0.5, 1.0
                                                  //| , 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5)
	
	val allNosTill10=DenseVector.range(0, 10) //> allNosTill10  : breeze.linalg.DenseVector[Int] = DenseVector(0, 1, 2, 3, 4, 
                                                  //| 5, 6, 7, 8, 9)
	//Fill
	val denseJust2s=DenseVector.fill(10, 2)   //> denseJust2s  : breeze.linalg.DenseVector[Int] = DenseVector(2, 2, 2, 2, 2, 2
                                                  //| , 2, 2, 2, 2)
  
  //Slice
  
  val fourThroughSevenIndexVector= allNosTill10.slice(4, 7)
                                                  //> fourThroughSevenIndexVector  : breeze.linalg.DenseVector[Int] = DenseVector(
                                                  //| 4, 5, 6)
  val twoThroughNineSkip2IndexVector= allNosTill10.slice(2, 9, 2)
                                                  //> twoThroughNineSkip2IndexVector  : breeze.linalg.DenseVector[Int] = DenseVect
                                                  //| or(2, 4, 6)
  //Creating from Scala Vector
  val vectFromArray=DenseVector(collection.immutable.Vector(1,2,3,4))
                                                  //> vectFromArray  : breeze.linalg.DenseVector[scala.collection.immutable.Vecto
                                                  //| r[Int]] = DenseVector(Vector(1, 2, 3, 4))
  
  //Vector Arithmetic
  
  //Dot product
  val justFive2s=DenseVector.fill(5, 2)           //> justFive2s  : breeze.linalg.DenseVector[Int] = DenseVector(2, 2, 2, 2, 2)
  val zeroThrough4=DenseVector.range(0, 5, 1)     //> zeroThrough4  : breeze.linalg.DenseVector[Int] = DenseVector(0, 1, 2, 3, 4)
                                                  //| 
	val dotVector=zeroThrough4.dot(justFive2s)//> dotVector  : Int = 20
	
	//Addition
	val additionVector=evenNosTill20 + denseJust2s
                                                  //> additionVector  : breeze.linalg.DenseVector[Int] = DenseVector(2, 4, 6, 8, 
                                                  //| 10, 12, 14, 16, 18, 20)
                                                  
	//Scalar addition
	val inPlaceValueAddition=evenNosTill20 +2 //> inPlaceValueAddition  : breeze.linalg.DenseVector[Int] = DenseVector(2, 4, 
                                                  //| 6, 8, 10, 12, 14, 16, 18, 20)
  
  //Scalar subtraction
	val inPlaceValueSubtraction=evenNosTill20 -2
                                                  //> inPlaceValueSubtraction  : breeze.linalg.DenseVector[Int] = DenseVector(-2,
                                                  //|  0, 2, 4, 6, 8, 10, 12, 14, 16)
	
	 
  //Scalar multiplication
	val inPlaceValueMultiplication=evenNosTill20 *2
                                                  //> inPlaceValueMultiplication  : breeze.linalg.DenseVector[Int] = DenseVector(
                                                  //| 0, 4, 8, 12, 16, 20, 24, 28, 32, 36)
  //Scalar division
	val inPlaceValueDivision=evenNosTill20 /2 //> inPlaceValueDivision  : breeze.linalg.DenseVector[Int] = DenseVector(0, 1, 
                                                  //| 2, 3, 4, 5, 6, 7, 8, 9)
	
	
	//Concatenate a Vector
	
	val concatVector=DenseVector.vertcat(zeroThrough4, justFive2s)
                                                  //> concatVector  : breeze.linalg.DenseVector[Int] = DenseVector(0, 1, 2, 3, 4,
                                                  //|  2, 2, 2, 2, 2)

	val concatVector1=DenseVector.horzcat(zeroThrough4, justFive2s)
                                                  //> concatVector1  : breeze.linalg.DenseMatrix[Int] = 0  2  
                                                  //| 1  2  
                                                  //| 2  2  
                                                  //| 3  2  
                                                  //| 4  2  
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
                            

	
	import breeze.stats._
	
	//Convert Vector of type Int to Vector of type Double
	val evenNosTill20Double=breeze.linalg.convert(evenNosTill20, Double)
                                                  //> evenNosTill20Double  : breeze.linalg.DenseVector[Double] = DenseVector(0.0,
                                                  //|  2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0)
	//Calculate Mean and Variance.  Note that this needs a Vector of Double
	meanAndVariance(evenNosTill20Double)      //> res0: breeze.stats.MeanAndVariance = MeanAndVariance(9.0,36.666666666666664
                                                  //| ,10)
	
	
	meanAndVariance(convert(evenNosTill20, Double))
                                                  //> res1: breeze.stats.MeanAndVariance = MeanAndVariance(9.0,36.666666666666664
                                                  //| ,10)
	   
	//Calculate Mean
  mean(evenNosTill20Double)                       //> res2: Double = 9.0
  
  //Calculate Standard Deviation
  stddev(evenNosTill20Double)                     //> res3: Double = 6.0553007081949835

	
	                                               
	//ERROR cases
	val fiveLength=DenseVector(1,2,3,4,5)     //> fiveLength  : breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4, 5)
  
  val tenLength=DenseVector.fill(10, 20)          //> tenLength  : breeze.linalg.DenseVector[Int] = DenseVector(20, 20, 20, 20, 2
                                                  //| 0, 20, 20, 20, 20, 20)
  
  fiveLength+tenLength                            //> res4: breeze.linalg.DenseVector[Int] = DenseVector(21, 22, 23, 24, 25)
  
  DenseVector.vertcat(fiveLength, tenLength)      //> res5: breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4, 5, 20, 20, 2
                                                  //| 0, 20, 20, 20, 20, 20, 20, 20)
  
   //DenseVector.horzcat(fiveLength, tenLength)
	
	
}