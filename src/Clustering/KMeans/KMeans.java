package Clustering.KMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KMeans {
  //2D K-Means implementation
  //TODO: Implement for n-dimensions

  //Computes centroid of list of given points
  private static Point getCentroid(List<Point> points) {
    int size = points.size();
    Point result = points.parallelStream().reduce(Point::sumPoints).get();

    result.setX(result.getX() / size);
    result.setY(result.getY() / size);

    return result;
  }

  //Returns points that have been assigned to specified cluster
  public static List<Point> getAssignedCluster(int index, List<Assignment>
      assignments) {
    return assignments.parallelStream().filter(i -> i.getCluster() == index)
        .map(i -> i.getPoint()).collect(Collectors.toList());
  }

  //Determines index of centroid closest to point. Centroids are 1 indexed
  //PRE: Centroids are given in ascending cluster index order
  private static Assignment assign(Point p, List<Point> centroids) {
    double minDistance = Integer.MAX_VALUE;
    int minIndex = 0;

    for (int i = 0; i < centroids.size(); i++) {
      if (p.distance(centroids.get(i)) < minDistance) {
        minDistance = p.distance(centroids.get(i));
        minIndex = i + 1;
      }
    }

    return new Assignment(p, minIndex);
  }

  //Associates each point to the cluster whose centroid is nearest to it
  private static List<Assignment> assignAll(List<Point> points, List<Point>
      centroids) {
    return points.parallelStream().map(i -> assign(i, centroids)).collect(Collectors
        .toList());
  }

  //Finds centroids for given assignment of points to clusters
  private static List<Point> findCentroids(List<Assignment> assignments, int
      numClusters) {
    assert(numClusters >= 1) : "Number of clusters must be a positive integer";

    List<Point> result = new ArrayList<>();

    IntStream.rangeClosed(1, numClusters).forEachOrdered(i ->
        result.add(getCentroid(getAssignedCluster(i, assignments))));

    return result;
  }

  //Performs single iteration of algorithm
  private static Clustering doIteration(List<Point> points, List<Point>
      centroids,
                                int numClusters) {
    List<Assignment> assignments = assignAll(points, centroids);

    return new Clustering(assignments, findCentroids(assignments, numClusters));
  }

  //Clusters the points into clusters using the k-means algorithm
  static Clustering cluster(List<Point> points, int numClusters) {
    assert (points.size() >= numClusters) : "There should always be at least " +
        "as many points as clusters";

    //initially use the first n (numClusters) points as centroids
    List<Point> initialCentroids = new ArrayList<>();
    for (int i = 0; i < numClusters; i++) {
      initialCentroids.add(points.get(i));
    }

    boolean clusterConverged;
    int iterationCounter = 0;

    //iterate until previous result "matches" the current result (Convergence)
    Clustering currentCluster = doIteration(points, initialCentroids, numClusters);
    do {
      Clustering prevCluster = currentCluster;
      currentCluster = doIteration(points, prevCluster.getCentroids(), numClusters);
      clusterConverged = prevCluster.equals(currentCluster);
      iterationCounter++;
    } while (!clusterConverged);

    System.out.println("\nNumber of iterations to reach convergence: " +
        iterationCounter);

    return currentCluster;
  }
}