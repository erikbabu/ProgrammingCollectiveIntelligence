package Clustering.KMeans;

import java.util.Arrays;
import java.util.List;


public class Clustering {
  private final List<Assignment> assignments;
  private final List<Point> centroids;

  Clustering(List<Assignment> assignments, List<Point> centroids) {
    this.assignments = assignments;
    this.centroids = centroids;
  }

  List<Assignment> getAssignments() {
    return assignments;
  }

  List<Point> getCentroids() {
    return centroids;
  }

  @Override
  public String toString() {
    return "(" + Arrays.toString(assignments.toArray()) + ", " + Arrays
        .toString(centroids.toArray()) +  ")";
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Clustering)) {
      return false;
    }

    Clustering thatClustering = (Clustering) that;

    List<Point> cents = thatClustering.getCentroids();
    List<Assignment> assignments = thatClustering.getAssignments();

    boolean centsCond = cents.containsAll(getCentroids())
        && getCentroids().containsAll(cents);
    boolean assignmentCond = assignments.containsAll(getAssignments())
        && getAssignments().containsAll(assignments);

    return centsCond && assignmentCond;
  }

  @Override
  public int hashCode() {
    int result = assignments.hashCode();
    result = 31 * result + centroids.hashCode();
    return result;
  }
}
