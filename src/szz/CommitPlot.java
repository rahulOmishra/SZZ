package szz;

import java.awt.Color;
import java.awt.BasicStroke;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.*;
import org.jfree.chart.labels.*;
import java.util.Set;
import org.jfree.chart.axis.*;
import java.awt.Shape;
import org.jfree.util.ShapeUtilities;

public class CommitPlot extends ApplicationFrame {


    public CommitPlot( String applicationTitle, String chartTitle,Set<Commit> fixCommit,Set<Commit> induceCommit )
    {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle ,
                "Commits" ,
                "" ,
                createDataset(fixCommit,induceCommit),
                PlotOrientation.VERTICAL ,
                true , true , false);

        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 800 , 500 ) );
        final XYPlot plot = xylineChart.getXYPlot( );
        //plot.setRangeAxis(0,null );
        //plot.setDomainAxis(0,null);
        Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.blue);
        renderer.setSeriesPaint( 1, Color.RED);

        renderer.setBaseShape(cross);
        renderer.setBasePositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE8, TextAnchor.CENTER));
        //renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f) );
        renderer.setBaseShapesFilled(false);
        ValueAxis range = plot.getRangeAxis();
        ValueAxis rangeV= plot.getDomainAxis();
        range.setVisible(false);
        rangeV.setVisible(false);
        range.setRange(0,1);
        //rangeV.setRange(0,1000000000);
        //renderer.setLegendItemToolTipGenerator(
             //   new StandardXYSeriesLabelGenerator("Legend {0}"));
        //renderer.setBaseShape(new Ellipse2D.Float(100.0f, 100.0f, 100.0f, 100.0f));;
        plot.setRenderer( renderer );
        setContentPane( chartPanel );
    }

    private XYDataset createDataset( Set<Commit> fixcommit,Set<Commit> induceCommit)
    {
        final XYSeries fixCommits = new XYSeries( "fix-commits" );

        for(Commit commit: fixcommit) {
            fixCommits.add(commit.getGitCommit().getCommitTime(), 0.7);
        }
        fixCommits.getAutoSort();
        final XYSeries induceCommits= new XYSeries("induce-commits");
        for(Commit commit: induceCommit) {
            induceCommits.add(commit.getGitCommit().getCommitTime(), 0.3);
        }
        induceCommits.getAutoSort();
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( fixCommits );
        dataset.addSeries(induceCommits);

        return dataset;
    }

    public static void showPlot(Set<Commit> fixCommit, Set<Commit> induceCommit)
    {
        CommitPlot chart = new CommitPlot("Bug Inducing relationship", "",fixCommit,induceCommit);
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}