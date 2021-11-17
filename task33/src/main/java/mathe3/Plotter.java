package mathe3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class Plotter {

    private final int nPoints;
    private final Plot plot;
    private final double xStep;
    private int plotNumber= 0;
    private Color[] colors = new Color[]{Color.BLACK, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.WHITE};
    private double padding = 0.5;
    public Plotter(List<Point> points, int nPoints) {
        double xMin = 0;
        double xMax = 0;
        double yMin = 0;
        double yMax = 0;
        for (Point point : points){
            if (point.x > xMax){
                xMax = point.x;
            }
            if (point.x < xMin){
                xMin = point.x;
            }
            if (point.y < yMin){
                yMin = point.y;
            }
            if (point.y > yMax){
                yMax = point.y;
            }
        }
        double maxDiff = Math.max(xMax - xMin, yMax - yMin);
        double minX = xMin - padding;
        double maxX = xMin + maxDiff + padding;
        double minY = yMin - padding;
        double maxY = yMin + maxDiff + padding;

        this.nPoints = nPoints;
        xStep = (maxX - minX) / this.nPoints;
        plot = Plot.plot(Plot.plotOpts().height(1024).width(1024))
        .xAxis("x", Plot.axisOpts().range(minX, maxX))
        .yAxis("y", Plot.axisOpts().range(minY, maxY))
        .series(UUID.randomUUID().toString(), Plot.data()
                .xy(0, minY)
                .xy(0, maxY), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK))
        .series(UUID.randomUUID().toString(), Plot.data()
                .xy(minX,0)
                .xy(maxX, 0), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK));
    }

    public Plotter function(DoubleUnaryOperator fx, double leftbound, double rightbound) {
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        for (double x = leftbound; x <= rightbound; x += xStep) {
            xs.add(x);
            ys.add(fx.applyAsDouble(x));
        }
        Plot.Data data = Plot.data()
                .xy(xs, ys);

        plot.series(UUID.randomUUID().toString(), data, Plot.seriesOpts().color(colors[plotNumber++]));
        return this;
    }

    public Plotter point(Point point){
        return points(List.of(point));
    }

    public Plotter points(List<Point> points){
        List<Double> xs = points.stream().map(p -> p.x).collect(Collectors.toList());
        List<Double> ys = points.stream().map(p -> p.y).collect(Collectors.toList());
        plot.series(UUID.randomUUID().toString(),
                Plot.data().xy(xs, ys),
                Plot.seriesOpts().markerColor(Color.GREEN)
        .marker(Plot.Marker.CIRCLE)
        .line(Plot.Line.NONE));
        return this;
    }

    public void save() {
        try {
            plot.save("result", "png");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save file");
        }
    }
}
