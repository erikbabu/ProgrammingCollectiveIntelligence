package Clustering.KMeans;

import org.jfree.ui.RefineryUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
  public static void main(String[] args) {

    List<Point> randomPoints = new ArrayList<>();
    int numRandomPoints = 50;
    int randomRange = 25;
    Random generator = new Random();
    int numClusters = 5;

    for (int i = 0; i < numRandomPoints; i++) {
      randomPoints.add(new Point(generator.nextInt(randomRange), generator
          .nextInt(randomRange)));
    }

    //DISPLAY RESULTS TO TERMINAL

    //PRE-ALGORITHM
    System.out.println("List of Points:");
    System.out.println(Arrays.toString(randomPoints.toArray()));

    //PLOT INITIAL GRAPH
    PointsPlot initialHeader = new PointsPlot("Initial 2-D Scatter Plot",
       randomPoints);
    initialHeader.pack();
    RefineryUtilities.centerFrameOnScreen(initialHeader);
    initialHeader.setVisible(true);

    //RUN ALGORITHM
    Clustering cluster = KMeans.cluster(randomPoints, numClusters);

    //POST-ALGORITHM
    System.out.println("\nPoints with their final cluster assignments:");
    System.out.println(cluster.getAssignments());
    System.out.println("\nFinal centroids for clusters 1-" + numClusters + ":");
    System.out.println(cluster.getCentroids());

    //PLOT FINAL GRAPH
    KMeansPlot finalHeader = new KMeansPlot("Final 2-D Scatter Plot", cluster);
    finalHeader.pack();
    RefineryUtilities.centerFrameOnScreen(finalHeader);
    finalHeader.setVisible(true);
  }
}
