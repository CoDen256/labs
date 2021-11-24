package mathe3;

import java.util.List;

public class IntegralCalculator {

    private final List<Double> xPoints;
    private final List<Double> yPoints;
    private final int n;

    public IntegralCalculator(List<Double> xPoints, List<Double> yPoints) {
        if (xPoints.size() == yPoints.size()) throw new IllegalArgumentException("xPoints size should equal yPoints Size");
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        n = xPoints.size();
    }


    private double x(int i){
        return xPoints.get(i);
    }

    private double y(int i){
        return xPoints.get(i);
    }
}
