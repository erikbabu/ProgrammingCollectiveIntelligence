package Clustering.KMeans.Plot;

//external library jfreechart used for plotting
//compilation requires jcommon-1.0.23 and jfreechart-1.0.19
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import Clustering.KMeans.Plot.DataSet.DataSet2D;
import Clustering.KMeans.Point;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class PointsPlot extends AbstractPlot {
  private final List<Point> points;

  //Constructor takes file header name and points data
  public PointsPlot(String header, List<Point> points) {
    super(header);
    this.points = points;
    JPanel panel = createDisplayPanel();
    panel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
    this.setContentPane(panel);
  }

  JPanel createDisplayPanel() {
    JFreeChart chart = createChart(new DataSet2D(points), true);
    ChartPanel panel = new ChartPanel(chart);
    panel.setMouseWheelEnabled(true);

    return panel;
  }
}
