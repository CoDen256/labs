package mathe3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Plotter {

    private final double Xmin;
    private final double Xmax;
    private final int Npoint;
    private List<Double> xs;
    private List<Double> ys;


    public Plotter(double xmin, double xmax, int npoint) {
        Xmin = xmin;
        Xmax = xmax;
        Npoint = npoint;
    }

    public Plotter plot(Function<Double, Double> fx){
        double xStep = (Xmax - Xmin) / Npoint;
        xs = new ArrayList<>();
        ys = new ArrayList<>();
        for (double x = Xmin; x <= Xmax; x += xStep){
            xs.add(x);
            ys.add(fx.apply(x));
        }
        return this;
    }

    public void save(){
        Plot plotObj = Plot.plot(null)
                .series(null, Plot.data()
                        .xy(xs, ys), null);
        try {
            plotObj.save("result", "png");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save file");
        }
    }
}
