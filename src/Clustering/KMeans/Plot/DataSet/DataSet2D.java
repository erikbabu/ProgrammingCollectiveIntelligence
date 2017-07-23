package Clustering.KMeans.Plot.DataSet;

import Clustering.KMeans.Point;
import java.util.List;

public class DataSet2D extends GeneralDataSet {
  private final int itemCount;

  public DataSet2D(List<Point> points) {
    super(points);
    this.itemCount = points.size();
  }

  @Override
  public Comparable getSeriesKey(int i) {
    return "Point";
  }

  @Override
  public int getItemCount(int i) {
    return itemCount;
  }
}
