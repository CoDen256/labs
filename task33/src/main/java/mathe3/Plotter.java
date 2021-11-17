package mathe3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class Plotter {

    private static final double padding = 0.5;
    private static final int HEIGHT = 768;
    private static final int WIDTH = 768;
    private final Random random = new Random();

    private final Plot plot;
    private final double step;

    public Plotter(List<Point> points, int nPoints) {
        double[] minMax = calculateBounds(points);
        double minX = minMax[0] - padding;
        double maxX = minMax[1] + padding;
        double minY = minMax[2] - padding;
        double maxY = minMax[3] + padding;
        step = (maxX - minX) / nPoints;
        plot = Plot
                .plot(Plot.plotOpts().height(HEIGHT).width(WIDTH))
                .xAxis("x", Plot.axisOpts().range(minX, maxX))
                .yAxis("y", Plot.axisOpts().range(minY, maxY))
                .series(randomName(), Plot.data()
                        .xy(0, minY)
                        .xy(0, maxY), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK))
                .series(randomName(), Plot.data()
                        .xy(minX, 0)
                        .xy(maxX, 0), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK));
    }

    private String randomName() {
        return UUID.randomUUID().toString();
    }

    private Color randomColor(){
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private double[] calculateBounds(List<Point> points) {
        double xMin = 0;
        double xMax = 0;
        double yMin = 0;
        double yMax = 0;
        for (Point point : points) {
            if (point.x > xMax) xMax = point.x;
            if (point.x < xMin) xMin = point.x;
            if (point.y < yMin) yMin = point.y;
            if (point.y > yMax) yMax = point.y;
        }
        double maxDiff = Math.max(xMax - xMin, yMax - yMin);
        return new double[]{xMin, xMin + maxDiff, yMin, yMin + maxDiff};
    }

    public Plotter function(DoubleUnaryOperator fx, double leftbound, double rightbound) {
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        for (double x = leftbound; x <= rightbound; x += step) {
            xs.add(x);
            ys.add(fx.applyAsDouble(x));
        }
        Plot.Data data = Plot.data().xy(xs, ys);

        plot.series(randomName(), data, Plot.seriesOpts().color(randomColor()));
        return this;
    }

    public Plotter point(Point point) {
        return points(List.of(point));
    }

    public Plotter points(List<Point> points) {
        List<Double> xs = points.stream().map(p -> p.x).collect(Collectors.toList());
        List<Double> ys = points.stream().map(p -> p.y).collect(Collectors.toList());
        plot.series(randomName(),
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
