import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Charter {
	public XYSeriesCollection data;
	public String title;
	public int width;
	public int height;
	
	private XYSeries[] series;
	
	
	public Charter(String title){
		data = new XYSeriesCollection();
		this.title = title;
		width = 800;
		height = 800;
		series = new XYSeries[2];
		series[0] = new XYSeries("Local Best Makespan");
		series[1] = new XYSeries("Global Best Makespan");
		data.addSeries(series[0]);
		data.addSeries(series[1]);
		
	}
	public Charter(){
		data = new XYSeriesCollection();
		this.title = "";
		width = 800;
		height = 800;
		series = new XYSeries[2];
		series[0] = new XYSeries("Local Mode Makespan");
		series[1] = new XYSeries("Global Best Makespan");
		data.addSeries(series[0]);
		data.addSeries(series[1]);
	}
	public void addValue(int val, int key, int time){
		series[key].add(time, val);
	}
	
	public void generateGraph(String filename) throws IOException{
		JFreeChart lineChartObject = ChartFactory.createXYLineChart(this.title, "Makespan", "Iterations", this.data);
		XYPlot plot = (XYPlot) lineChartObject.getPlot();
		NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
		yAxis.setAutoRangeIncludesZero(false);
		yAxis.setAutoRange(true);
		NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRange(true);
		plot.setBackgroundPaint(Color.WHITE);
		
		File chart = new File(filename+".png");
		ChartUtilities.saveChartAsPNG(chart, lineChartObject, this.width, this.height);
		
	}
	
}
