package Clustering.KMeans.Plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPlot extends ApplicationFrame {
  protected static final int CHART_WIDTH = 2000;
  protected static final int CHART_HEIGHT = 2000;
  protected static final int dispVal = 2;

  AbstractPlot(String title) {
    super(title);
  }

  //Creates and returns 2-D Chart with plotted values
  protected static JFreeChart createChart(XYDataset dataset, boolean regPlot) {

    JFreeChart chart = (regPlot) ?
        ChartFactory.createScatterPlot("Points before Clustering", "X", "Y",
            dataset, PlotOrientation.VERTICAL, true, true, false)
        :
        ChartFactory.createScatterPlot("Points after 2D K-Means Clustering",
            "X", "Y", dataset,
            PlotOrientation.VERTICAL, true, true, false);

    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setNoDataMessage("NO DATA");
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
    plot.setDomainZeroBaselineVisible(true);
    plot.setRangeZeroBaselineVisible(true);
    plot.setDomainGridlineStroke(new BasicStroke(0));
    plot.setDomainMinorGridlineStroke(new BasicStroke(0));
    plot.setDomainGridlinePaint(Color.blue);
    plot.setRangeGridlineStroke(new BasicStroke(0));
    plot.setRangeMinorGridlineStroke(new BasicStroke(0));
    plot.setRangeGridlinePaint(Color.blue);
    plot.setDomainMinorGridlinesVisible(true);
    plot.setRangeMinorGridlinesVisible(true);
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
        .getRenderer();
    renderer.setSeriesOutlinePaint(0, Color.black);
    renderer.setUseOutlinePaint(true);
    NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
    xAxis.setAutoRangeIncludesZero(false);
    xAxis.setTickMarkInsideLength(dispVal);
    xAxis.setTickMarkOutsideLength(dispVal);
    xAxis.setMinorTickCount(dispVal);
    xAxis.setMinorTickMarksVisible(true);
    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setTickMarkInsideLength(dispVal);
    yAxis.setTickMarkOutsideLength(dispVal);
    yAxis.setMinorTickCount(dispVal);
    yAxis.setMinorTickMarksVisible(true);

    return chart;
  }

  abstract JPanel createDisplayPanel();
}
