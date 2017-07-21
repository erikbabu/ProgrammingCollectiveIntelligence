package Clustering.KMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KMeans {
  //2D K-Means implementation
  //TODO: Implement for n-dimensions and set up graphs to visualise data

  //Computes centroid of list of given points
  static Point getCentroid(List<Point> points) {
    int size = points.size();
    Point result = points.parallelStream().reduce(Point::sumPoints).get();

    result.setX(result.getX() / size);
    result.setY(result.getY() / size);

    return result;
  }

  //Returns points that have been assigned to specified cluster
  static List<Point> getAssignedCluster(int index, List<Assignment>
      assignments) {
    return assignments.parallelStream().filter(i -> i.getCluster() == index)
        .map(i -> i.getPoint()).collect(Collectors.toList());
  }

  //Determines index of centroid closest to point. Centroids are 1 indexed
  //PRE: Centroids are given in ascending cluster index order
  static Assignment assign(Point p, List<Point> centroids) {
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
  static List<Assignment> assignAll(List<Point> points, List<Point> centroids) {
    return points.parallelStream().map(i -> assign(i, centroids)).collect(Collectors
        .toList());
  }

  //Finds centroids for given assignment of points to clusters
  static List<Point> findCentroids(List<Assignment> assignments, int numClusters) {
    assert(numClusters >= 1) : "Number of clusters must be a positive integer";

    List<Point> result = new ArrayList<>();

    IntStream.rangeClosed(1, numClusters).forEachOrdered(i ->
        result.add(getCentroid(getAssignedCluster(i, assignments))));

    return result;
  }

  //Performs single iteration of algorithm
  static Clustering doIteration(List<Point> points, List<Point> centroids,
                                int numClusters) {
    List<Assignment> assignments = assignAll(points, centroids);

    return new Clustering(assignments, findCentroids(assignments, numClusters));
  }


  //Clusters the points into clusters using the k-means algorithm
  static Clustering cluster(List<Point> points, int numClusters) {
    assert (points.size() >= numClusters) : "There should always be at least " +
        "as many points as clusters";

    List<Point> initialCentroids = new ArrayList<>();

    for (int i = 0; i < numClusters; i++) {
      initialCentroids.add(points.get(i));
    }

    boolean clusterConverged = false;
    int iterationCounter = 0;
    Clustering currentCluster = doIteration(points, initialCentroids, numClusters);

    do {
      Clustering prevCluster = currentCluster;
      currentCluster = doIteration(points, prevCluster.getCentroids(), numClusters);
      clusterConverged = prevCluster.equals(currentCluster);
      iterationCounter++;
    } while (!clusterConverged);

    System.out.println("Number of iterations to reach convergence: " + iterationCounter);
    return currentCluster;
  }


  public static void main(String[] args) {
    List<Point> ps1 = new ArrayList<>();
    Point p1 = new Point(2, 3);
    Point p2 = new Point(3, 5);
    Point p3 = new Point(6, 7);
    Point p4 = new Point(4, 8);
    Point p5 = new Point(9, 3);
    Point p6 = new Point(1, 1);
    Point p7 = new Point(2, 1);
    Point p8 = new Point(9,1);
    Point p9 = new Point(7, 2);
    Point p10 = new Point(5, 6);
    Point p11 = new Point(5, 9);

    ps1.add(p1);
    ps1.add(p2);
    ps1.add(p3);
    ps1.add(p4);
    ps1.add(p5);
    ps1.add(p6);
    ps1.add(p7);
    ps1.add(p8);
    ps1.add(p9);
    ps1.add(p10);
    ps1.add(p11);

    Clustering cluster = cluster(ps1, 3);
    System.out.println("\nPoints with their final cluster assignments");
    System.out.println(cluster.getAssignments());
    System.out.println("\nFinal centroids for clusters 1-" + cluster
        .getCentroids().size());
    System.out.println(cluster.getCentroids());
  }
}
