package mathe3;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class LagrangeCalculator {


    private final List<Double> xs;
    private final List<Double> ys;
    private final int nPoints; // total amount of points
    private final int n;       // the degree

    public LagrangeCalculator(List<Double> xs, List<Double> ys) {
        if (xs.size() != ys.size()) throw new IllegalArgumentException("x points and y points should be equal sizes");
        this.xs = xs;
        this.ys = ys;
        nPoints = xs.size();
        n = nPoints - 1;
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

    public DoubleUnaryOperator lagrangePolynom(){
        return x -> {
            double sum = 0;
            for (int i = 0; i < nPoints; i++) {
                sum += y(i) * lagrange(i).applyAsDouble(x);
            }
            return sum;
        };
    }

    private double x(int i){
        return xs.get(i);
    }

    private double y(int i){
        return ys.get(i);
    }

}
