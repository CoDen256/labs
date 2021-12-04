package mathe3;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import mathe3.plot.Point;

public class Task51ABC {

    public static void main(String[] args) {



    }


    // actual y(x)
    public static double y(double x){
        return 89 / 218. * Math.exp(-3. / 2 * x) - 3 / 109. * Math.sin(5 * x) + 10 / 109. * Math.cos(5 * x);
    }

    // y'(x)
    public static double yDerivative(double x, double y){
        return -3. / 2 * y - 0.5 * Math.sin(x);
    }

    public static Map<Double, Double> convertPointsToMap(List<Point> pointList){
        return pointList.stream().collect(Collectors.toMap(Point::getX, Point::getY));
    }

}
