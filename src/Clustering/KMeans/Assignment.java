package Clustering.KMeans;

public class Assignment {
  private Point point;
  private int cluster;

  Assignment(Point point, int cluster) {
    this.point = point;
    this.cluster = cluster;
  }

  Point getPoint() {
    return point;
  }

  int getCluster() {
    return cluster;
  }

  @Override
  public String toString() {
    return "(" + getPoint() + ", " + getCluster() + ")";
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Assignment)) {
      return false;
    }

    Assignment thatAssignment = (Assignment) that;

    return (thatAssignment.getPoint().equals(getPoint())) && (thatAssignment
        .getCluster() == getCluster());
  }

  @Override
  public int hashCode() {
    int result = point != null ? point.hashCode() : 0;
    result = 31 * result + cluster;
    return result;
  }
}
