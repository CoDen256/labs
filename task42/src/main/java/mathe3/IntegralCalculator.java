package mathe3;

import java.util.List;

public class IntegralCalculator {

    private final List<Double> xs;
    private final List<Double> ys;
    private final int nPoints; // number of points
    private final int n;       // degree

    public IntegralCalculator(List<Double> xs, List<Double> ys) {
        if (xs.size() != ys.size()) throw new IllegalArgumentException("xPoints size should equal yPoints Size");
        this.xs = xs;
        this.ys = ys;
        nPoints = xs.size();
        n = nPoints - 1;
    }


    private double x(int i){
        return xs.get(i);
    }

    private double y(int i){
        return xs.get(i);
    }

    public double calculate() {
        return 0;
    }
}
