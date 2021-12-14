package mathe3;
import java.util.List;
import mathe3.plot.Plotter;
import mathe3.plot.Point;
public class Task51D {
    private static final double FROM = 0;
    private static final double TO = 4*Math.PI;
    public static final Point START_CONDITION = new Point(0, 1/5.);

    // a little bit different start conditions
    public static final Point START_CONDITION1 = new Point(0, 2/5.);
    public static final Point START_CONDITION2 = new Point(0, -1/5.);

    public static final int N = 1000;
    public static final double H = ((TO - FROM)/N);

    public static void main(String[] args) {
        List<Point> approxEuler = new EulerMethod().
                calculatePoints(Task51D::yDerivative, START_CONDITION, H, N);
        new Plotter(new double[]{-16, 16, -16, 16}, 500000, 0.5)
                .function(x -> y(x, START_CONDITION))
                .function(x -> y(x, START_CONDITION1))
                .function(x -> y(x, START_CONDITION2))
                .points(approxEuler)
                .save();
    }

    // y'(x)
    public static double yDerivative(double x, double y){
        return y - Math.cos(2*x);
    }
    // actual y(x) for start condition
    public static double y(double x, Point startCondition){
        return (startCondition.y - 1./5) * Math.exp(x) + 1. / 5 * (Math.cos(2 * x) - 2 * Math.sin(2 * x));
    }
}