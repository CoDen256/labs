package mathe3;
import java.util.List;
import mathe3.plot.Plotter;
import mathe3.plot.Point;
public class Task51D {
    private static final double FROM = 0;
    private static final double TO = 4*Math.PI;
    public static final Point START_CONDITION = new Point(0, 1/5.);
    public static final int N = 1000;
    public static final double H = ((TO - FROM)/N);

    public static void main(String[] args) {
        List<Point> approxEuler = new EulerMethod().
                calculatePoints(Task51D::yDerivative, START_CONDITION, H, 0);
        new Plotter(new double[]{-2, 16, -16, 2}, 500000, 0.5)
                .function(Task51D::y)
                .points(approxEuler)
                .save(3);
    }

    // y'(x)
    public static double yDerivative(double x, double y){
        return y - Math.cos(2*x);
    }
    // actual y(x)
    public static double y(double x){
        return 0*Math.exp(x) + 1. / 5 * (Math.cos(2 * x) - 2 * Math.sin(2 * x));
    }
}