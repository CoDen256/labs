package mathe3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import mathe3.plot.Point;

public class HeunMethod {
    public List<Point> calculatePoints(DoubleBinaryOperator diffFunction, Point condition, double h, int n){
        List<Point> points = new ArrayList<>();
        points.add(condition);

        Point prevPoint = condition;
        for(int i = 1; i <= n; i++){
            double prevX = prevPoint.x; // x for i
            double prevY = prevPoint.y; // y for i

            double x = prevX + h;       // x for i+1

            double slope = diffFunction.applyAsDouble(prevX, prevY); // f(xi, yi)
            double predictor = prevY + h * slope;  // predictor `y` for i+1

            double predictorSlope = diffFunction.applyAsDouble(x, predictor); // f(x(i+1), `y`(i+1))
            double corrector = prevY + 0.5 * h * slope + 0.5 * h * predictorSlope;    // corrector y for i+1

            prevPoint = new Point(x, corrector);
            points.add(prevPoint);
        }

        return points;
    }

}
