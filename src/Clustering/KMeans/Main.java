package Clustering.KMeans;

import org.jfree.ui.RefineryUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
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

    Point initCent1 = new Point(3, 1);
    Point initCent2 = new Point(2, 9);
    Point initCent3 = new Point(7, 8);
    List<Point> initCents = Arrays.asList(initCent1, initCent2, initCent3);

    //DISPLAY RESULTS TO TERMINAL

    //PRE-ALGORITHM
    System.out.println("Initial assignments:");
    System.out.println(KMeans.assignAll(ps1, initCents));
    System.out.println("\nInitial centroids:");
    System.out.println(initCents);

    //PLOT INITIAL GRAPH
    List<Assignment> initialAssignments = KMeans.assignAll(ps1, initCents);
    KMeansPlot initialHeader = new KMeansPlot("Initial 2-D Scatter Plot",
        new Clustering(initialAssignments, initCents));
    initialHeader.pack();
    RefineryUtilities.centerFrameOnScreen(initialHeader);
    initialHeader.setVisible(true);

    //RUN ALGORITHM
    Clustering cluster = KMeans.cluster(ps1, 5);

    //POST-ALGORITHM
    System.out.println("\nPoints with their final cluster assignments:");
    System.out.println(cluster.getAssignments());
    System.out.println("\nFinal centroids for clusters 1-" + cluster
        .getCentroids().size() + ":");
    System.out.println(cluster.getCentroids());

    //PLOT FINAL GRAPH
    KMeansPlot finalHeader = new KMeansPlot("Final 2-D Scatter Plot", cluster);
    finalHeader.pack();
    RefineryUtilities.centerFrameOnScreen(finalHeader);
    finalHeader.setVisible(true);
  }
}
