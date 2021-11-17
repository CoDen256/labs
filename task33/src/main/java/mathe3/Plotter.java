package mathe3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Plotter {

    private final double Xmin;
    private final double Xmax;
    private final int Npoint;
    private final Plot plot;
    private final double xStep;


    public Plotter(double xmin, double xmax, int npoint) {
        Xmin = xmin;
        Xmax = xmax;
        Npoint = npoint;
        xStep = (Xmax - Xmin) / Npoint;
        plot = Plot.plot(null);
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

        plot.series(UUID.randomUUID().toString(), data, null);
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
