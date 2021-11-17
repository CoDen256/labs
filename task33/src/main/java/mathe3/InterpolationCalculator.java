package mathe3;

import java.util.List;
import java.util.function.Function;

public class InterpolationCalculator {

    private final List<Point> points;
    private final double slopeY0;
    private final double slopeYN;

    public InterpolationCalculator(List<Point> points, double slopeY0, double slopeYN) {
        this.points = points;
        this.slopeY0 = slopeY0;
        this.slopeYN = slopeYN;
    }

    public List<Double> calculateSlopes(){

        return List.of();
    }


    private double a(int i){
        return 2 / (x(i+1) - x(i));

    }
    private double b(int i){
        return 4 * (1/(x(i+1)-x(i)) + 1/(x(i+2)-x(i+1)));
    }
    private double c(int i){
        return 2/(x(i+2)-x(i+1));
    }

    private double r(int i){
        double p1 = -6 * y(i) / sq(x(i + 1) - x(i));
        double p2 = 6 * y(i+1) / sq(x(i + 1) - x(i));
        double p3 = -6 * y(i+1) / sq(x(i + 2) - x(i + 1));
        double p4 = 6 * y(i+2) / sq(x(i + 2) - x(i + 1));
        return p1 + p2 + p3 + p4;
    }

    private Function<Double, Double> t(int i){
        return x -> (x - x(i)) / (x(i + 1) - x(i));
    }

    private double alpha1(double t){
        return 1 - 3 * sq(t) + 2 * cb(t);
    }

    private double alpha2(double t){
        return 3 * sq(t) - 2 * cb(t);
    }

    private double alpha3(double t){
        return t - 2 * sq(t) + cb(t);
    }

    private double alpha4(double t){
        return - sq(t) + cb(t);
    }

    private double x(int i){
        return points.get(i).x;
    }

    private double y(int i){
        return points.get(i).y;
    }

    private static double sq(double val){
        return val * val;
    }

    private static double cb(double val){
        return val * val;
    }
}
