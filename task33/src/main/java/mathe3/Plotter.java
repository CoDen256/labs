package mathe3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class Plotter {

    private final double Xmin;
    private final double Xmax;
    private final int Npoint;
    private final Plot plot;
    private final double xStep;
    private int plotNumber= 0;
    private Color[] colors = new Color[]{Color.BLACK, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.WHITE};

    public Plotter(double xmin, double xmax, int npoint) {
        Xmin = xmin;
        Xmax = xmax;
        Npoint = npoint;
        xStep = (Xmax - Xmin) / Npoint;
        plot = Plot.plot(Plot.plotOpts().height(1024).width(1024))
        .xAxis("x", Plot.axisOpts().range(-5, 5))
        .yAxis("y", Plot.axisOpts().range(-5, 5))
        .series(UUID.randomUUID().toString(), Plot.data()
                .xy(0, -5)
                .xy(0, 5), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK))
        .series(UUID.randomUUID().toString(), Plot.data()
                .xy(-5,0)
                .xy(5, 0), Plot.seriesOpts().line(Plot.Line.DASHED).color(Color.BLACK));
    }

    public Plotter function(DoubleUnaryOperator fx) {
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        for (double x = Xmin; x <= Xmax; x += xStep) {
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
