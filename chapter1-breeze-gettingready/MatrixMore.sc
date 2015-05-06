import breeze.linalg._

object MatrixMore {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val simpleMatrix=DenseMatrix((4.0,7.0),(3.0,-5.0))
                                                  //> simpleMatrix  : breeze.linalg.DenseMatrix[Double] = 4.0  7.0   
                                                  //| 3.0  -5.0  
  
  
  val firstVector=simpleMatrix(::,0)              //> firstVector  : breeze.linalg.DenseVector[Double] = DenseVector(4.0, 3.0)
	
	val secondVector=simpleMatrix(::,1)       //> secondVector  : breeze.linalg.DenseVector[Double] = DenseVector(7.0, -5.0)
	
	
	val firstVectorByCols=simpleMatrix(0 to 1,0)
                                                  //> firstVectorByCols  : breeze.linalg.DenseVector[Double] = DenseVector(4.0, 3.
                                                  //| 0)
	
	
	val firstRowFirstCol=simpleMatrix(0,0)    //> firstRowFirstCol  : Double = 4.0
	
	val firstRowStatingCols=simpleMatrix(0,0 to 1)
                                                  //> firstRowStatingCols  : breeze.linalg.Transpose[breeze.linalg.DenseVector[Dou
                                                  //| ble]] = Transpose(DenseVector(4.0, 7.0))
	
	
	val firstRowAllCols=simpleMatrix(0,::)    //> firstRowAllCols  : breeze.linalg.Transpose[breeze.linalg.DenseVector[Double]
                                                  //| ] = Transpose(DenseVector(4.0, 7.0))
	
	
	val secondRow=simpleMatrix(1,::)          //> secondRow  : breeze.linalg.Transpose[breeze.linalg.DenseVector[Double]] = Tr
                                                  //| anspose(DenseVector(3.0, -5.0))
	
	
	val transpose=simpleMatrix.t              //> transpose  : breeze.linalg.DenseMatrix[Double] = 4.0  3.0   
                                                  //| 7.0  -5.0  
                                                  
	val inverse=inv(simpleMatrix)             //> May 06, 2015 1:46:47 PM com.github.fommil.jni.JniLoader liberalLoad
                                                  //| INFO: successfully loaded /var/folders/9x/0gm9f7hx03gcv48nzs6n1r_c0000gn/T/j
                                                  //| niloader7836888042675984803netlib-native_system-osx-x86_64.jnilib
                                                  //| inverse  : breeze.linalg.DenseMatrix[Double] = 0.12195121951219512  0.170731
                                                  //| 70731707318  
                                                  //| 0.07317073170731708  -0.0975609756097561  
	
	
	simpleMatrix * inverse                    //> May 06, 2015 1:46:47 PM com.github.fommil.jni.JniLoader load
                                                  //| INFO: already loaded netlib-native_system-osx-x86_64.jnilib
                                                  //| res0: breeze.linalg.DenseMatrix[Double] = 1.0                     0.0  
                                                  //| -5.551115123125783E-17  1.0  
	
	val denseEig=eig(simpleMatrix)            //> denseEig  : breeze.linalg.eig.DenseEig = Eig(DenseVector(5.922616289332565, 
                                                  //| -6.922616289332565),DenseVector(0.0, 0.0),0.9642892971721949   -0.5395744865
                                                  //| 143975  
                                                  //| 0.26485118719604456  0.8419378679586305   )
  val eigenVectors=denseEig.eigenvectors          //> eigenVectors  : breeze.linalg.DenseMatrix[Double] = 0.9642892971721949   -0.
                                                  //| 5395744865143975  
                                                  //| 0.26485118719604456  0.8419378679586305   
  val eigenValues=denseEig.eigenvalues            //> eigenValues  : breeze.linalg.DenseVector[Double] = DenseVector(5.92261628933
                                                  //| 2565, -6.922616289332565)
  
  
  val matrixToEigVector=simpleMatrix*denseEig.eigenvectors (::,0)
                                                  //> matrixToEigVector  : breeze.linalg.DenseVector[Double] = DenseVector(5.71111
                                                  //| 54990610915, 1.568611955536362)
  val vectorToEigValue=denseEig.eigenvectors(::,0) * denseEig.eigenvalues (0)
                                                  //> vectorToEigValue  : breeze.linalg.DenseVector[Double] = DenseVector(5.711115
                                                  //| 4990610915, 1.5686119555363618)
                                                  
                                                  
                                                  
	val identityMatrix=DenseMatrix.eye[Int](4)//> identityMatrix  : breeze.linalg.DenseMatrix[Int] = 1  0  0  0  
                                                  //| 0  1  0  0  
                                                  //| 0  0  1  0  
                                                  //| 0  0  0  1  

	//Concatenate a Matrix - Vertically
	//val vertConcatMatrix=DenseMatrix.vertcat(identityMatrix, simpleMatrix)
  //val horzConcatMatrix=DenseMatrix.horzcat(identityMatrix, simpleMatrix)
  
  
}