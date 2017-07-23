package Clustering.KMeans;

//external library jfreechart used for plotting
//compilation requires jcommon-1.0.23 and jfreechart-1.0.19
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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

public class KMeansPlot extends ApplicationFrame {

  private static final int CHART_WIDTH = 2000;
  private static final int CHART_HEIGHT = 2000;
  private static final int dispVal = 2;
  private final Clustering cluster;

  //Constructor takes file header name and cluster data
  KMeansPlot(String header, Clustering cluster) {
    super(header);
    this.cluster = cluster;
    JPanel panel = createDisplayPanel();
    panel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
    this.setContentPane(panel);
  }

  //Creates and returns 2-D chart with plotted values
  private static JFreeChart createChart(XYDataset dataset) {

    JFreeChart chart = ChartFactory.createScatterPlot("2 Dimensional K-Means " +
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
    JFreeChart chart = createChart(new KMeansDataSet(cluster));
    ChartPanel panel = new ChartPanel(chart);
    panel.setMouseWheelEnabled(true);

    return panel;
  }

  private class KMeansDataSet extends AbstractXYDataset implements XYDataset,
      DomainInfo, RangeInfo {
    private final Integer[][] xValues;
    private final Integer[][] yValues;
    private int[] clusterSizes;
    private final int seriesCount;
    private final Number domainMin;
    private final Number domainMax;
    private final Number rangeMin;
    private final Number rangeMax;
    private final Range domainRange;
    private final Range range;

    KMeansDataSet(Clustering cluster) {

      List<Assignment> assignmentsSorted = cluster.getAssignments().stream().
          sorted(Comparator.comparingInt(Assignment::getCluster))
          .collect(Collectors.toList());

      List<Point> centroids = cluster.getCentroids();

      int maxXValue = assignmentsSorted.stream().map(i -> i.getPoint().getX())
          .reduce(Math::max).get();
      int maxYValue = assignmentsSorted.stream().map(i -> i.getPoint().getY())
          .reduce(Math::max).get();
      int numClusters = centroids.size();
      this.clusterSizes = new int[numClusters + 1];
      this.seriesCount = numClusters + 1;
      this.domainMin = 0.0;
      this.domainMax = maxXValue + dispVal;
      this.domainRange = new Range(domainMin.doubleValue(), domainMax
          .doubleValue());
      this.rangeMin = 0.0;
      this.rangeMax = maxYValue + dispVal;
      this.range = new Range(rangeMin.doubleValue(), rangeMax.doubleValue());

      for (int i = 0; i < numClusters; i++) {
        clusterSizes[i] = KMeans.getAssignedCluster(i + 1, assignmentsSorted)
            .size();
      }

      clusterSizes[numClusters] = numClusters;

      int mostPointsPerCluster = Arrays.stream(clusterSizes).reduce
          (Math::max).getAsInt();

      this.xValues = new Integer[seriesCount][mostPointsPerCluster];
      this.yValues = new Integer[seriesCount][mostPointsPerCluster];

      int acc = 0;
      for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < clusterSizes[i]; j++) {
          Point currentPoint = assignmentsSorted.get(acc).getPoint();
          xValues[i][j] = currentPoint.getX();
          yValues[i][j] = currentPoint.getY();

          acc++;
        }
      }

      for (int i = 0; i < numClusters; i++) {
        xValues[seriesCount - 1][i] = centroids.get(i).getX();
        yValues[seriesCount - 1][i] = centroids.get(i).getY();
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
      if (i < clusterSizes.length - 1) {
        return "Cluster" + (i + 1);
      }

      return "Centroids";
    }

    @Override
    public int getItemCount(int i) {
      return clusterSizes[i];
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