package mathe3;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                if (Double.isInfinite(product)){
                    product = Double.MIN_NORMAL; // workaround so product won't be -Infinity
                }
            }

            return product;
        };
    }

    public DoubleUnaryOperator lagrange1(int k){ // not working somehow
        return x -> {
            double upper = 1;
            double lower = 1;

            for (int i = 0; i < nPoints; i++) {
                if (i == k) continue;
                upper *= x - x(i);
                lower *= x(k) - x(i);
            }
            lower = lower == 0 ? Double.MIN_VALUE : lower;
            return upper / lower;
        };
    }

    public DoubleUnaryOperator lagrange2(int k){ // too long
        return x -> {
            BigDecimal lower = BigDecimal.ONE;
            BigDecimal upper = BigDecimal.ONE;
            for (int i = 0; i < nPoints; i++) {
                if (i == k) continue;
                upper = upper.multiply(BigDecimal.valueOf(x - x(i)));
                lower = lower.multiply(BigDecimal.valueOf(x(k) - x(i)));
            }
            return upper.divide(lower).doubleValue();
        };
    }

    public DoubleUnaryOperator lagrangePolynomial(){
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
