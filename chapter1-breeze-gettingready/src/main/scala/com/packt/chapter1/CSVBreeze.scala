package com.packt.chapter1

import breeze.linalg._
import java.io.File

object CSVBreeze extends App{
  
  //I am assuming that the file is at the root of the project.  Optionally, you could specify the full path of the file too
  
  //Read the commma separated file named WWWusage.csv. Skip the header please
  val usageMatrix=csvread(file=new File("WWWusage.csv"), separator=',', skipLines=1)
  //print first five rows
  println ("Usage matrix \n"+ usageMatrix(0 to 5,::)) 

/*
1.0  1.0  88.0  
2.0  2.0  84.0  
3.0  3.0  85.0  
4.0  4.0  85.0  
5.0  5.0  84.0  
6.0  6.0  85.0  
*/
  
  //Lets save the first two rows into a separate file. Optionally, we could manipulate the values too
  val firstColumnSkipped= usageMatrix(::, 1 to usageMatrix.cols-1)
  
  //Sample some data so as to ensure we are fine
  println ("First Column skipped \n"+ firstColumnSkipped(0 to 5, ::))
/*
1.0  88.0  
2.0  84.0  
3.0  85.0  
4.0  85.0  
5.0  84.0  
6.0  85.0  
*/
  //Write this modified matrix to a file
  csvwrite(file=new File ("firstColumnSkipped.csv"), mat=firstColumnSkipped, separator=',')
  
  //Reading a Vector - this is funny. Just use the apply method of the Matrix to slice off a Vector from the Matrix (as we saw in the previous recipe on Matrices)
  val vectorFromMatrix=csvread(file=new File("firstColumnSkipped.csv"), separator=',', skipLines=0)(::,1)
  
  println ("Vector from Matrix \n"+ vectorFromMatrix.slice(0, 10, 1))
//DenseVector(88.0, 84.0, 85.0, 85.0, 84.0, 85.0, 83.0, 85.0, 88.0, 89.0)
  
  //Let's write our Vector back. Unfortunately, we have to convert this to a matrix before that (simple though)
  csvwrite(file=new File ("firstVector.csv"), mat=vectorFromMatrix.toDenseMatrix, separator=',')
  
  
}