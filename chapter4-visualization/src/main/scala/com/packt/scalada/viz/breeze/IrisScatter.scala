package com.packt.scalada.viz.breeze

import io.continuum.bokeh.ColumnDataSource
import breeze.linalg._
import java.io.File
import io.continuum.bokeh.Color
import io.continuum.bokeh.Plot
import io.continuum.bokeh.Document
import io.continuum.bokeh.GlyphRenderer
import io.continuum.bokeh.Diamond
import io.continuum.bokeh.DataRange1d
import io.continuum.bokeh.LinearAxis
import io.continuum.bokeh._

object IrisScatter extends App {
  //First lets load our iris data 
  import BreezeSource._
  import source._

   //Create the plot with the title "Petal length vs Width" 
  val plot = new Plot().title("Iris Petal Length vs Width")

  //Let's create a marker object to mark the data point 
  val diamond = new Diamond()
    .x(petal_length)
    .y(petal_width)
    .fill_color(speciesColor)
    .fill_alpha(0.5)
    .size(10)

  //Let's compose the main graph
  val dataPointRenderer = new GlyphRenderer().data_source(source).glyph(diamond)

    //Set Data range for the X and the Y Axis
  val xRange = new DataRange1d().sources(petal_length :: Nil)
  val yRange = new DataRange1d().sources(petal_width :: Nil)
  plot.x_range(xRange).y_range(yRange)

  //X and Y Axis
  val xAxis = new LinearAxis().plot(plot).axis_label("Petal Length").bounds((1.0, 7.0))
  val yAxis = new LinearAxis().plot(plot).axis_label("Petal Width").bounds((0.0, 2.5))
  val xgrid = new Grid().plot(plot).axis(xAxis).dimension(0)
  val ygrid = new Grid().plot(plot).axis(yAxis).dimension(1)
  
  plot.below <<= (listRenderer => (xAxis :: listRenderer))
  plot.left <<= (listRenderer => (yAxis :: listRenderer))
  
  //Tools
  val panTool = new PanTool().plot(plot)
  val wheelZoomTool = new WheelZoomTool().plot(plot)
  val previewSaveTool = new PreviewSaveTool().plot(plot)
  val resetTool = new ResetTool().plot(plot)
  val resizeTool = new ResizeTool().plot(plot)
  val crosshairTool = new CrosshairTool().plot(plot)
  
  //Legends - Manual :-(
  val setosa = new Diamond().fill_color(Color.Red).size(10).fill_alpha(0.5)
  val setosaGlyphRnd=new GlyphRenderer().glyph(setosa)
  val versicolor = new Diamond().fill_color(Color.Green).size(10).fill_alpha(0.5)
  val versicolorGlyphRnd=new GlyphRenderer().glyph(versicolor)
  val virginica = new Diamond().fill_color(Color.Blue).size(10).fill_alpha(0.5)
  val virginicaGlyphRnd=new GlyphRenderer().glyph(virginica)
  
  val legends = List("setosa" -> List(setosaGlyphRnd),
		  			"versicolor" -> List(versicolorGlyphRnd),
		  			"virginica" -> List(virginicaGlyphRnd))
		  			
  val legend = new Legend().orientation(LegendOrientation.TopLeft).plot(plot).legends(legends)
  
  //Add the renderers and the tools to the plot
  plot.renderers := List(xAxis, yAxis, dataPointRenderer, xgrid, ygrid, legend, setosaGlyphRnd, virginicaGlyphRnd, versicolorGlyphRnd)
  plot.tools := List(panTool, wheelZoomTool, previewSaveTool, resetTool, resizeTool, crosshairTool)
  
  val document = new Document(plot)
  val file = document.save("IrisBokehBreeze.html")

  println(s"Saved the chart as ${file.url}")
}

object BreezeSource {

  val iris = csvread(file = new File("irisNumeric.csv"), separator = ',')

  val colormap = Map[Int, Color](0 -> Color.Red, 1 -> Color.Green, 2 -> Color.Blue)

  object source extends ColumnDataSource {
    val sepal_length = column(iris(::, 0)) //First column, all rows
    val sepal_width = column(iris(::, 1))
    val petal_length = column(iris(::, 2))
    val petal_width = column(iris(::, 3))
    val species = column(iris(::, 4))
    val speciesColor = column(iris(::, 4).map(each=>colormap(each.toInt)))
  }

}
