package mathe3;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class IntegralCalculator {

    private final List<Double> xs;
    private final List<Double> ys;
    private final int nPoints; // number of points
    private final int n;       // degree

    public IntegralCalculator(List<Double> xs, List<Double> ys) {
        if (xs.size() != ys.size()) throw new IllegalArgumentException("x points and y points should be equal sizes");
        this.xs = xs;
        this.ys = ys;
        nPoints = xs.size();
        n = nPoints - 1;
    }

    public double integrate(double from, double to) {
        double sum = 0;
        for (int k = 0; k < nPoints-1; k++) {
            sum += y(k) * g(k, this::integrateByTrapezoidalRule, from, to);
        }
        return sum;
    }



    private double g(int k, IntegrationFunction numIntegrationMethod, double from, double to){
        return numIntegrationMethod.integrate(new LagrangeCalculator(xs, ys).lagrange(k), from, to);
    }

    public double integrateByTrapezoidalRule(DoubleUnaryOperator fx, double from, double to){
        double sum = 0;
        double h = (to - from) / n;
        for (int k = 0; k < nPoints-1; k++) {
            sum += h/2 * (fx.applyAsDouble(x(k)) + fx.applyAsDouble(x(k+1)));
        }
        return sum;
    }

    public double integrateByTrapezoidalRule2(DoubleUnaryOperator fx, double from, double to){
        return 0.5 * (y(0) + y(nPoints-1));
    }

    private double x(int i){
        return xs.get(i);
    }

    private double y(int i){
        return ys.get(i);
    }


}
