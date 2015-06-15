package com.packt.scalada.viz.breeze

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.databricks.spark.csv._
import io.continuum.bokeh._
import org.joda.time.format.DateTimeFormat

object StockPricesTSApp {

  def main(args: Array[String]) {
	  val conf = new SparkConf().setAppName("csvDataFrame").setMaster("local[2]")
	  val sc = new SparkContext(conf)
	  val sqlContext = new SQLContext(sc)
	  val stocks = sqlContext.csvFile(filePath = "dow_jones_index.data", useHeader = true, delimiter = ',')
	  stocks.registerTempTable("stocks")
	
	  val microsoftPlot = plotWeeklyClosing(sqlContext, "MSFT", Color.Blue)
	  val bofaPlot = plotWeeklyClosing(sqlContext, "BAC", Color.Red)
	  val caterPillarPlot = plotWeeklyClosing(sqlContext, "CAT", Color.Orange)
	  val mmmPlot = plotWeeklyClosing(sqlContext, "MMM", Color.Black)
	
	  val msDocument = new Document(microsoftPlot)
	  val msHtml = msDocument.save("MicrosoftClosingPrices.html")
	
	  println(s"Saved the Microsoft Chart as ${msHtml.url}")
	  
	  
	  val children = List(List(microsoftPlot, bofaPlot), List(caterPillarPlot, mmmPlot))
	  val grid = new GridPlot().children(children)
	  
	  val document = new Document(grid)
	  val html = document.save("DJClosingPrices.html")
	  
	  println(s"Saved 4 Grid stock chart as ${html.url}")
	  
  }
  
  def plotWeeklyClosing(sqlContext:SQLContext, ticker: String, color: Color) = {

    val source = StockSource.getSource(ticker, sqlContext)
    
    import source._

    //Create Plot
    val plot = new Plot().title(ticker.toUpperCase()).width(800).height(400)
    
    //Fixing Data ranges
    val xdr = new DataRange1d().sources(List(date))
    val ydr = new DataRange1d().sources(List(close))
    plot.x_range(xdr).y_range(ydr)

    //Line
    val line = new Line().x(date).y(close).line_color(color).line_width(2)
    val lineGlyph = new GlyphRenderer().data_source(source).glyph(line)
    
    //Axis and Grids
    val xformatter = new DatetimeTickFormatter().formats(Map(DatetimeUnits.Months -> List("%b %Y")))
    val xaxis = new DatetimeAxis().plot(plot).formatter(xformatter).axis_label("Month")
    val yaxis = new LinearAxis().plot(plot).axis_label("Price")
    plot.below <<= (xaxis :: _)
    plot.left <<= (yaxis :: _)
    val xgrid = new Grid().plot(plot).dimension(0).axis(xaxis)
    val ygrid = new Grid().plot(plot).dimension(1).axis(yaxis)

    //Tools
    val panTool = new PanTool().plot(plot)
    val wheelZoomTool = new WheelZoomTool().plot(plot)
    val previewSaveTool = new PreviewSaveTool().plot(plot)
    val resetTool = new ResetTool().plot(plot)
    val resizeTool = new ResizeTool().plot(plot)
    val crosshairTool = new CrosshairTool().plot(plot)

    //Legend
    val legends = List(ticker -> List(lineGlyph))
    val legend = new Legend().plot(plot).legends(legends)

    //Adding renderers and Tools
    plot.renderers <<= (xaxis :: yaxis :: xgrid :: ygrid :: lineGlyph :: legend :: _)
    plot.tools := List(panTool, wheelZoomTool, previewSaveTool, resetTool, resizeTool, crosshairTool)

    plot

  }

}

object StockSource {
  
  val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
  
  def getSource(ticker: String, sqlContext: SQLContext) = {
    val stockDf = sqlContext.sql(s"select stock, date, close from stocks where stock= '$ticker'")
    stockDf.cache()
    
    val dateData: Array[Double] = stockDf.select("date").collect.map(eachRow => formatter.parseDateTime(eachRow.getString(0)).getMillis().toDouble)
    val closeData: Array[Double] = stockDf.select("close").collect.map(eachRow => eachRow.getString(0).drop(1).toDouble)

    object source extends ColumnDataSource {
      val date = column(dateData)
      val close = column(closeData)
    }
    source
  }
}