package szz;

import java.awt.Color;
import java.awt.BasicStroke;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeriesCollection;
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
import org.jfree.data.time.*;
import java.awt.Graphics2D;
import org.jfree.chart.annotations.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import org.jfree.chart.axis.*;
import java.awt.Shape;
import org.jfree.util.ShapeUtilities;
import org.jfree.data.time.TimeSeries;

public class CommitPlot extends ApplicationFrame {



    public CommitPlot(String applicationTitle, String chartTitle, Map<Commit, List<Commit>> blameMap ) throws ParseException {
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
        chartPanel.setForeground(Color.black);

        XYShapeAnnotation annotation = new XYShapeAnnotation(new Ellipse2D.Float(100.0f, 100.0f, 100.0f, 100.0f), new BasicStroke(1.0f), Color.blue);
        XYPointerAnnotation pointer = new XYPointerAnnotation("arrow", 0.5,0.5,0.0);

        XYPlot plot = xylineChart.getXYPlot( );
        plot.addAnnotation(pointer);
        plot.addAnnotation(annotation);


        StandardXYToolTipGenerator ttG =
                new StandardXYToolTipGenerator("", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"), new DecimalFormat("0.00") );


        DateAxis dateAxis = new DateAxis();

        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd MM yyyy hh:mm:ss zzz"));
        plot.setDomainAxis(dateAxis);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);








        Shape cross = ShapeUtilities.createDiamond(4);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
//        renderer.setSeriesPaint( 0 , Color.blue);
//        renderer.setSeriesPaint( 1, Color.RED);
        //renderer.setBaseShape(cross);
        renderer.setBasePositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE8, TextAnchor.CENTER));
        //renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

        renderer.setBaseToolTipGenerator(ttG);
        renderer.setBaseShapesFilled(true);
        renderer.setBaseShapesVisible(true);



        ValueAxis range = plot.getRangeAxis();
        ValueAxis rangeD= plot.getDomainAxis();
        range.setVisible(false);
        rangeD.setVisible(true);
        range.setRange(0,plot.getSeriesCount()+2);
        Graphics2D g2;
        for(int i=0;i<plot.getSeriesCount();i++){
            renderer.setSeriesShape(i,cross);
            renderer.setSeriesPaint(i,Color.BLUE);
        }


        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }


    private XYDataset createDataset( Map<Commit, List<Commit>>  blameMap) throws ParseException {

        String name;
        int count=0;
        int yValue=1;
        XYSeriesCollection dataset = new XYSeriesCollection( );
        //TimeSeriesCollection dataset=new TimeSeriesCollection();
        for( Map.Entry<Commit, List<Commit>> entry : blameMap.entrySet()) {
            Commit key = entry.getKey();
            Set<Commit> dedupeCommit = new HashSet<>();
            XYSeries seriesCommits = new XYSeries( "series"+count);
            //TimeSeries seriesCommits = new TimeSeries("series"+count);
            count++;
            dedupeCommit.add(key);
            for (Commit value : entry.getValue()) {
                dedupeCommit.add(value);

            }
            for(Commit commit:dedupeCommit){





//                long yourSeconds = commit.getGitCommit().getCommitTime();
//                Date date = new Date(yourSeconds * 1000L);
//                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
//                String formatted = format.format(date);
//                Date date1= format.parse(formatted);
               // RegularTimePeriod regularP = new Day (newDate);
                //TimeSeriesDataItem tsData = new TimeSeriesDataItem (newDate.getTime(), yValue);

                seriesCommits.add(commit.getGitCommit().getCommitterIdent().getWhen().getTime(),yValue);
            }
            yValue++;

            dataset.addSeries(seriesCommits);


        }
        return dataset;
    }


    public static void showPlot(Map<Commit, List<Commit>>  blameMap) throws ParseException {
        CommitPlot chart = new CommitPlot("Bug Inducing relationship", "",blameMap);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}