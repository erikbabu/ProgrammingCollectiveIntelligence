package Clustering.KMeans.Plot;

//external library jfreechart used for plotting
//compilation requires jcommon-1.0.23 and jfreechart-1.0.19
import java.awt.Dimension;
import javax.swing.JPanel;
import Clustering.KMeans.Clustering;
import Clustering.KMeans.Plot.DataSet.KMeansDataSet;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class KMeansPlot extends AbstractPlot {
  private final Clustering cluster;

  //Constructor takes file header name and cluster data
  public KMeansPlot(String header, Clustering cluster) {
    super(header);
    this.cluster = cluster;
    JPanel panel = createDisplayPanel();
    panel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
    this.setContentPane(panel);
  }

  @Override
  JPanel createDisplayPanel() {
    JFreeChart chart = createChart(new KMeansDataSet(cluster), false);
    ChartPanel panel = new ChartPanel(chart);
    panel.setMouseWheelEnabled(true);

    return panel;
  }
}