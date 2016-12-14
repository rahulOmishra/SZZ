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

import java.util.*;

import org.jfree.chart.axis.*;
import java.awt.Shape;
import org.jfree.util.ShapeUtilities;
import java.text.NumberFormat;

public class CommitPlot extends ApplicationFrame {



    public CommitPlot(String applicationTitle, String chartTitle, Map<Commit, List<Commit>> blameMap ){
          //  public CommitPlot( String applicationTitle, String chartTitle,Set<Commit> fixCommit,Set<Commit> induceCommit ){

            super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle ,
                "Commits" ,
                "" ,
                createDataset( blameMap),
                PlotOrientation.VERTICAL ,
                false , true , false);

        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 800 , 500 ) );
        final XYPlot plot = xylineChart.getXYPlot( );
        Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
//        renderer.setSeriesPaint( 0 , Color.blue);
//        renderer.setSeriesPaint( 1, Color.RED);
        renderer.setBaseShape(cross);
        renderer.setBasePositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE8, TextAnchor.CENTER));
        renderer.setBaseShapesFilled(false);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        ValueAxis range = plot.getRangeAxis();
        ValueAxis rangeD= plot.getDomainAxis();
        range.setVisible(false);
        rangeD.setVisible(false);
        range.setRange(0,10);


        plot.setRenderer( renderer );
        setContentPane( chartPanel );
    }

//    private XYDataset createDataset( Set<Commit> fixcommit,Set<Commit> induceCommit){
//        final XYSeries fixCommits = new XYSeries( "fix-commits" );
//
//        for(Commit commit: fixcommit) {
//            fixCommits.add(commit.getGitCommit().getCommitTime(), 0.7);
//            System.out.println(commit.getGitCommit().getCommitTime());
//        }
//        fixCommits.getAutoSort();
//        final XYSeries induceCommits= new XYSeries("induce-commits");
//        for(Commit commit: induceCommit) {
//            induceCommits.add(commit.getGitCommit().getCommitTime(), 0.3);
//            System.out.println(commit.getGitCommit().getCommitTime());
//        }
//        induceCommits.getAutoSort();
//        final XYSeriesCollection dataset = new XYSeriesCollection( );
//        dataset.addSeries( fixCommits );
//        dataset.addSeries(induceCommits);
//
//        return dataset;
//    }
    private XYDataset createDataset( Map<Commit, List<Commit>>  blameMap){

        String name;
        int count=0;
        int yValue=1;
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        for( Map.Entry<Commit, List<Commit>> entry : blameMap.entrySet()) {
            Commit key = entry.getKey();
            Set<Commit> dedupeCommit = new HashSet<>();
            XYSeries seriesCommits = new XYSeries( "series"+count);
            count++;
            dedupeCommit.add(key);
            for (Commit value : entry.getValue()) {
                dedupeCommit.add(value);

            }
            for(Commit commit:dedupeCommit){
                seriesCommits.add(commit.getGitCommit().getCommitTime(),yValue);
            }
            yValue++;

            dataset.addSeries(seriesCommits);


        }
        return dataset;
    }
//    public static void showPlot(Set<Commit> fixCommit, Set<Commit> induceCommit) {
//        CommitPlot chart = new CommitPlot("Bug Inducing relationship", "",fixCommit,induceCommit);
//        chart.pack( );
//        RefineryUtilities.centerFrameOnScreen( chart );
//        chart.setVisible( true );
//    }

    public static void showPlot(Map<Commit, List<Commit>>  blameMap) {
        CommitPlot chart = new CommitPlot("Bug Inducing relationship", "",blameMap);
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}