package mathe3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import mathe3.plot.Point;

public class EulerMethod {
    public List<Point> calculatePoints(DoubleBinaryOperator derivativeFunction, Point condition, double h, int n){
        List<Point> points = new ArrayList<>();
        points.add(condition);

        Point prevPoint = condition;
        for(int i = 1; i <= n; i++){
            double slope = derivativeFunction.applyAsDouble(prevPoint.x, prevPoint.y); // f(xi, yi)

            double x = prevPoint.x + h;         // x for i+1
            double y = prevPoint.y + h * slope; // y for i+1

            prevPoint = new Point(x, y);
            points.add(prevPoint);
        }
        return points;
    }
}
