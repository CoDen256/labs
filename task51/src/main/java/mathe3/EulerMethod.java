package mathe3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import mathe3.plot.Point;

public class EulerMethod {
    public List<Point> calculatePoints(DoubleBinaryOperator diffFunction, Point condition, double from, double to, double h){
        List<Point> points = new ArrayList<>();
        points.add(condition);

        Point prevPoint = condition;
        int n = (int) ((from - to)/h); // number of points
        for(int i = 1; i <= n; i++){
            double slope = diffFunction.applyAsDouble(prevPoint.x, prevPoint.y); // f(xi, yi)

            double x = prevPoint.x + h;         // x for i+1
            double y = prevPoint.y + h * slope; // y for i + 1

            prevPoint = new Point(x, y);
            points.add(prevPoint);
        }
        return points;
    }
}
