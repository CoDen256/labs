package mathe3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class WeightsCalculator {

    private final List<Double> xs;
    private final int nPoints; // number of points

    public WeightsCalculator(List<Double> xs) {
        this.xs = xs;
        nPoints = xs.size();
    }

    public List<Double> calculateWeights(double from, double to, int m) {
    	List<Double> gs = new ArrayList<>();
        for (int k = 0; k < nPoints; k++) {
            DoubleUnaryOperator Lk = lagrange(k); // Lk(x) function of lagrange polynomial depending on x
            double g = integrateByTrapezoidalRule(Lk, from, to, m); // integrate Lk(x)
            gs.add(g);
        }
        return gs;
    }

    public double integrateByTrapezoidalRule(DoubleUnaryOperator fx, double from, double to, int m){
        double sum = 0;
        double h = (to - from) / m;
        for (double x = from; x <= to; x+=h) {
            sum += h/2 * (fx.applyAsDouble(x) + fx.applyAsDouble(x));
        }
        return sum;
    }

    public DoubleUnaryOperator lagrange(int k){
        return x -> {
            double product = 1;

            for (int i = 0; i < nPoints; i++) {
                if (i == k) continue;
                double upper = x - x(i);
                double lower = x(k) - x(i);
                product *= upper / lower;
            }

            return product;
        };
    }

    private double x(int i){
        return xs.get(i);
    }
}
