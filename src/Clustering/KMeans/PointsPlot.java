package Clustering.KMeans;

//external library jfreechart used for plotting
//compilation requires jcommon-1.0.23 and jfreechart-1.0.19
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.DomainInfo;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractXYDataset;

public class PointsPlot extends ApplicationFrame {

  private static final int CHART_WIDTH = 2000;
  private static final int CHART_HEIGHT = 2000;
  private static final int dispVal = 2;
  private final List<Point> points;

  //Constructor takes file header name and points data
  PointsPlot(String header, List<Point> points) {
    super(header);
    this.points = points;
    JPanel panel = createDisplayPanel();
    panel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
    this.setContentPane(panel);
  }

  //Creates and returns 2-D chart with plotted values
  private static JFreeChart createChart(XYDataset dataset) {

    JFreeChart chart = ChartFactory.createScatterPlot("Points before KMeans " +
            "Clustering", "X", "Y", dataset,
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

  JPanel createDisplayPanel() {
    JFreeChart chart = createChart(new DataSet2D(points));
    ChartPanel panel = new ChartPanel(chart);
    panel.setMouseWheelEnabled(true);

    return panel;
  }

  private class DataSet2D extends AbstractXYDataset implements XYDataset,
      DomainInfo, RangeInfo {
    private final Integer[][] xValues;
    private final Integer[][] yValues;
    private final int seriesCount;
    private final Number domainMin;
    private final Number domainMax;
    private final Number rangeMin;
    private final Number rangeMax;
    private final Range domainRange;
    private final Range range;

    DataSet2D(List<Point> points) {

      int maxXValue = points.stream().map(Point::getX).reduce(Math::max).get();
      int maxYValue = points.stream().map(Point::getY).reduce(Math::max).get();
      int numPoints = points.size();
      this.seriesCount = 1;
      this.domainMin = 0.0;
      this.domainMax = maxXValue + dispVal;
      this.domainRange = new Range(domainMin.doubleValue(), domainMax
          .doubleValue());
      this.rangeMin = 0.0;
      this.rangeMax = maxYValue + dispVal;
      this.range = new Range(rangeMin.doubleValue(), rangeMax.doubleValue());
      this.xValues = new Integer[seriesCount][numPoints];
      this.yValues = new Integer[seriesCount][numPoints];

      //store the data in 2D arrays for x and y values
      //outer array is points number. inner array stores x/y val
      for (int i = 0; i < numPoints; i++) {
          xValues[0][i] = points.get(i).getX();
          yValues[0][i] = points.get(i).getY();
        }
      }

    @Override
    public Number getX(int cluster, int index) {
      return xValues[cluster][index];
    }

    @Override
    public Number getY(int cluster, int index) {
      return yValues[cluster][index];
    }

    @Override
    public int getSeriesCount() {
      return seriesCount;
    }

    @Override
    public Comparable getSeriesKey(int i) {
      return "Point";
    }

    @Override
    public int getItemCount(int i) {
      return points.size();
    }

    @Override
    public double getDomainLowerBound(boolean b) {
      return domainMin.doubleValue();
    }

    @Override
    public double getDomainUpperBound(boolean b) {
      return domainMax.doubleValue();
    }

    @Override
    public Range getDomainBounds(boolean b) {
      return domainRange;
    }

    @Override
    public double getRangeLowerBound(boolean b) {
      return rangeMin.doubleValue();
    }

    @Override
    public double getRangeUpperBound(boolean b) {
      return rangeMax.doubleValue();
    }

    @Override
    public Range getRangeBounds(boolean b) {
      return range;
    }
  }
}
