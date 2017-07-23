package Clustering.KMeans.Plot.DataSet;

import Clustering.KMeans.Clustering;

public class KMeansDataSet extends GeneralDataSet {

  public KMeansDataSet(Clustering cluster) {
    super(cluster);
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
}