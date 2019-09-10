import java.io.IOException;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/**
 * @author timmolter
 */
public class SimpleRealTime {
    double[] xData, yData;
    String typeOfGraph;

    public SimpleRealTime(double[] xData, double[] yData, String typeOfGraph) {
        this.xData = xData;
        this.yData = yData;
        this.typeOfGraph = typeOfGraph;
    }

    public void makeGraph() throws IOException{


        // Create Chart
        XYChart chart = QuickChart.getChart(typeOfGraph, "X", "Y", "time", xData, yData);

        // Show it
        new SwingWrapper<XYChart>(chart).displayChart();

        // Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.PNG);

        // or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
    }
}

//import java.awt.BorderLayout;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.SwingConstants;
//import javax.swing.WindowConstants;
//import org.knowm.xchart.XChartPanel;
//import org.knowm.xchart.XYChart;
//import org.knowm.xchart.XYChartBuilder;
//import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
//import org.knowm.xchart.style.Styler.LegendPosition;
//
///** @author timmolter */
//public class SimpleRealTime {
//
//    public static void main(String[] args) {
//
//        // Create Chart
//        final XYChart chart =
//                new XYChartBuilder()
//                        .width(600)
//                        .height(400)
//                        .title("Area Chart")
//                        .xAxisTitle("X")
//                        .yAxisTitle("Y")
//                        .build();
//
//        // Customize Chart
//        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
//        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
//
//        // Series
//        chart.addSeries("a", new double[] {0, 3, 5, 7, 9}, new double[] {-3, 5, 9, 6, 5});
//        chart.addSeries("b", new double[] {0, 2, 4, 6, 9}, new double[] {-1, 6, 4, 0, 4});
//        chart.addSeries("c", new double[] {0, 1, 3, 8, 9}, new double[] {-2, -1, 1, 0, 1});
//
//        // Schedule a job for the event-dispatching thread:
//        // creating and showing this application's GUI.
//        javax.swing.SwingUtilities.invokeLater(
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        // Create and set up the window.
//                        JFrame frame = new JFrame("Advanced Example");
//                        frame.setLayout(new BorderLayout());
//                        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//                        // chart
//                        JPanel chartPanel = new XChartPanel<XYChart>(chart);
//                        frame.add(chartPanel, BorderLayout.CENTER);
//
//                        // label
//                        JLabel label = new JLabel("Blah blah blah.", SwingConstants.CENTER);
//                        frame.add(label, BorderLayout.SOUTH);
//
//                        // Display the window.
//                        frame.pack();
//                        frame.setVisible(true);
//                    }
//                });
//    }
//}