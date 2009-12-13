package openjchart.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import openjchart.DrawablePanel;
import openjchart.Legend;
import openjchart.DrawableConstants.Location;
import openjchart.DrawableConstants.Orientation;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.data.filters.Convolution;
import openjchart.data.filters.Filter;
import openjchart.data.filters.Kernel;
import openjchart.data.filters.Median;
import openjchart.plots.Plot;
import openjchart.plots.XYPlot;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.util.Insets2D;
import openjchart.util.KernelUtils;

public class ConvolutionExample extends JFrame {

	public ConvolutionExample() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Double.class, Double.class);
		Random r = new Random();
		for (int i=0; i<200; i++) {
			double x = i/2.0/3.141;
			double yError = Math.sqrt(3.0*0.1)*r.nextGaussian();
			double y = 10.0*Math.sin(x/5.0) + yError*yError*yError;
			data.add(x, y);
		}
		DataSeries ds = new DataSeries("Data", data, 0, 1);

		final double KERNEL_VARIANCE = 10.0;

		Kernel kernelLowpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize();
		Filter dataLowpass = new Convolution(data, kernelLowpass, Filter.Mode.MODE_REPEAT, 1);
		DataSeries dsLowpass = new DataSeries("Lowpass", dataLowpass, 0, 1);

		Kernel kernelHighpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize().negate().add(new Kernel(1.0));
		Filter dataHighpass = new Convolution(data, kernelHighpass, Filter.Mode.MODE_REPEAT, 1);
		DataSeries dsHighpass = new DataSeries("Highpass", dataHighpass, 0, 1);

		int kernelMovingAverageSize = (int)Math.round(2.0*KERNEL_VARIANCE);
		Kernel kernelMovingAverage = KernelUtils.getUniform(kernelMovingAverageSize, kernelMovingAverageSize - 1, 1.0).normalize();
		Filter dataMovingAverage = new Convolution(data, kernelMovingAverage, Filter.Mode.MODE_OMIT, 1);
		DataSeries dsMovingAverage = new DataSeries("Moving Average", dataMovingAverage, 0, 1);

		int kernelMovingMedianSize = (int)Math.round(2.0*KERNEL_VARIANCE);
		Filter dataMovingMedian = new Median(data, kernelMovingMedianSize, kernelMovingMedianSize - 1, Filter.Mode.MODE_OMIT, 1);
		DataSeries dsMovingMedian = new DataSeries("Moving Median", dataMovingMedian, 0, 1);

		XYPlot plot = new XYPlot(ds, dsLowpass, dsHighpass, dsMovingAverage, dsMovingMedian);

		plot.setShapeRenderer(ds, null);
		DefaultLineRenderer2D lineData = new DefaultLineRenderer2D();
		lineData.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0f, 0f, 0f));
		plot.setLineRenderer(ds, lineData);

		plot.setShapeRenderer(dsLowpass, null);
		DefaultLineRenderer2D lineLowpass = new DefaultLineRenderer2D();
		lineLowpass.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(1.0f, 0.2f, 0.0f));
		plot.setLineRenderer(dsLowpass, lineLowpass);

		plot.setShapeRenderer(dsHighpass, null);
		DefaultLineRenderer2D lineHighpass = new DefaultLineRenderer2D();
		lineHighpass.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0.2f, 0.4f, 0.8f));
		plot.setLineRenderer(dsHighpass, lineHighpass);

		plot.setShapeRenderer(dsMovingAverage, null);
		DefaultLineRenderer2D lineMovingAverage = new DefaultLineRenderer2D();
		lineMovingAverage.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0f, 0.67f, 0f));
		plot.setLineRenderer(dsMovingAverage, lineMovingAverage);

		plot.setShapeRenderer(dsMovingMedian, null);
		DefaultLineRenderer2D lineMovingMedian = new DefaultLineRenderer2D();
		lineMovingMedian.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0.5f, 0f, 0.5f));
		plot.setLineRenderer(dsMovingMedian, lineMovingMedian);

		plot.setSetting(Plot.KEY_LEGEND, true);
		plot.setSetting(Plot.KEY_LEGEND_POSITION, Location.SOUTH_WEST);
		plot.getLegend().setSetting(Legend.KEY_ORIENTATION, Orientation.HORIZONTAL);

		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new DrawablePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(getContentPane().getMinimumSize());
		setSize(900, 600);
	}

	public static void main(String[] args) {
		ConvolutionExample test = new ConvolutionExample();
		test.setVisible(true);
	}
}
