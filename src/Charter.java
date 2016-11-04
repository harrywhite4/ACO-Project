import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
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
	public List<Pair> legend;
	
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
		legend = new ArrayList<Pair>();
		
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
		legend = new ArrayList<Pair>();
	}
	public void addValue(int val, int key, int time){
		series[key].add(time, val);
	}
	
	public void addLegend(String key, double value){
		Pair p = new Pair(key, value);
		legend.add(p);
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
		final LegendItemCollection items = new LegendItemCollection();
		for (Pair p : legend){
			LegendItem item = new LegendItem(p.getKey() + " = " + p.getValue());
			items.add(item);
			item.setShapeVisible(false);
		}
		LegendItemSource source = new LegendItemSource() {

			@Override
			public LegendItemCollection getLegendItems() {
				return items;
			}
			
		};
		lineChartObject.addLegend(new LegendTitle(source));
		
		File chart = new File(filename+".png");
		ChartUtilities.saveChartAsPNG(chart, lineChartObject, this.width, this.height);
		
	}
	
}
