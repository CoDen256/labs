package mathe3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import mathe3.plot.Point;

public class EulerMethod {

    public EulerMethod() {
    }

    public List<Point> calculatePoints(DoubleBinaryOperator diffFunction, Point condition, double from, double to, double h){
        List<Point> points = new ArrayList<>();
        points.add(condition);

        Point prevPoint = condition;
        int n = (int) ((from - to)/h); // number of points
        for(int i = 1; i <= n; i++){
            double slope = diffFunction.applyAsDouble(prevPoint.x, prevPoint.y);
            Point nextPoint = addVectorToPoint(prevPoint, h, h * slope);

            prevPoint = nextPoint;
            points.add(nextPoint);
        }
        return points;
    }

    private Point addVectorToPoint(Point original, double vectorX, double vectorY){
        return new Point(original.x + vectorX, original.y + vectorY);
    }

}
