package com.packt.scalada.viz.breeze

import java.io.File
import breeze.linalg._
import breeze.linalg.DenseMatrix
import breeze.plot._
import java.awt.Color

object BreezeScatterPlot extends App{

  val iris=csvread(file=new File("irisNumeric.csv"), separator=',')
  
  val figure=Figure()
  val plot1=figure.subplot(0)
  
  //Make a plot of Length vs Width
  //plot1 += plot(iris(::,2), iris(::,3))
  //plot1 += scatter(iris(::,2), iris(::,3))
  plot1+=scatter(iris(::,2), iris(::,3), ((_:Int)=>100.0), ((_:Int)=>Color.ORANGE))

  plot1.xlabel="Petal Length (in cm)"
  plot1.xlabel="Petal Width (in cm)"
  
  figure.saveas("Petal vs Width.png")
}