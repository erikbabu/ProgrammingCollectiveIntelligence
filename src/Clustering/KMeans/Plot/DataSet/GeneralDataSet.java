package Clustering.KMeans.Plot.DataSet;

import Clustering.KMeans.Assignment;
import Clustering.KMeans.Clustering;
import Clustering.KMeans.KMeans;
import Clustering.KMeans.Point;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GeneralDataSet extends AbstractXYDataset implements XYDataset,
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
  protected int[] clusterSizes;

  public GeneralDataSet(Clustering cluster) {
    //assignments sorted by cluster id in ascending order
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
    this.domainMax = maxXValue;
    this.domainRange = new Range(domainMin.doubleValue(), domainMax
        .doubleValue());
    this.rangeMin = 0.0;
    this.rangeMax = maxYValue;
    this.range = new Range(rangeMin.doubleValue(), rangeMax.doubleValue());

    //store how many items belong to each cluster
    for (int i = 0; i < numClusters; i++) {
      clusterSizes[i] = KMeans.getAssignedCluster(i + 1, assignmentsSorted)
          .size();
    }

    //store number of centroids as well (for plotting)
    clusterSizes[numClusters] = numClusters;

    int mostPointsPerCluster = Arrays.stream(clusterSizes).reduce
        (Math::max).getAsInt();

    this.xValues = new Integer[seriesCount][mostPointsPerCluster];
    this.yValues = new Integer[seriesCount][mostPointsPerCluster];

    //store the data in 2D arrays for x and y values
    //outer array is cluster number. inner array stores x/y val
    int acc = 0;
    for (int i = 0; i < numClusters; i++) {
      for (int j = 0; j < clusterSizes[i]; j++) {
        Point currentPoint = assignmentsSorted.get(acc).getPoint();
        xValues[i][j] = currentPoint.getX();
        yValues[i][j] = currentPoint.getY();

        acc++;
      }
    }

    //store centroid values as well for plotting
    for (int i = 0; i < numClusters; i++) {
      xValues[seriesCount - 1][i] = centroids.get(i).getX();
      yValues[seriesCount - 1][i] = centroids.get(i).getY();
    }
  }

  public GeneralDataSet(List<Point> points) {
    int maxXValue = points.stream().map(Point::getX).reduce(Math::max).get();
    int maxYValue = points.stream().map(Point::getY).reduce(Math::max).get();
    int numPoints = points.size();
    this.seriesCount = 1;
    this.domainMin = 0.0;
    this.domainMax = maxXValue;
    this.domainRange = new Range(domainMin.doubleValue(), domainMax
        .doubleValue());
    this.rangeMin = 0.0;
    this.rangeMax = maxYValue;
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
