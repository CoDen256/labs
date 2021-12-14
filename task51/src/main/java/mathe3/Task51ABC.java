package mathe3;

import java.util.List;
import mathe3.plot.Point;

public class Task51ABC {

    public static final EulerMethod EULER_METHOD = new EulerMethod();
    public static final HeunMethod HEUN_METHOD = new HeunMethod();

    private static final double FROM = 0;
    private static final double TO = 2 * Math.PI;
    public static final Point START_CONDITION = new Point(0, 0.5);

    public static void main(String[] args) {
        int n = 4;
        for (int i = 0; i < 16; i ++ ){
            calculate(n);
            n *= 2;
        }
    }

    private static void calculate(int n) {
        double h = ((TO - FROM)/n);

        System.out.printf("%nH = %.10f ", h);
        System.out.println("N = "+n);
        List<Point> approxEuler = EULER_METHOD.calculatePoints(Task51ABC::yDerivative, START_CONDITION, h, n);
        Point lastEuler = approxEuler.get(approxEuler.size() - 1);
        List<Point> approxHeun = HEUN_METHOD.calculatePoints(Task51ABC::yDerivative, START_CONDITION, h, n);
        Point lastHeun = approxHeun.get(approxHeun.size() - 1);
        double actualLast = y(TO);

        double eulerDiff = Math.abs(actualLast - lastEuler.y);
        System.out.printf("Euler diff: %.10f%n", eulerDiff);

        double heunDiff = Math.abs(actualLast - lastHeun.y);
        System.out.printf("Heun diff: %.10f%n", heunDiff);
    }


    // actual y(x)
    public static double y(double x){
        return 89. / 218. * Math.exp(-3. / 2 * x) - 3 / 109. * Math.sin(5 * x) + 10 / 109. * Math.cos(5 * x);
    }

    // y'(x)
    public static double yDerivative(double x, double y){
        return -3. / 2. * y - 0.5 * Math.sin(5*x);
    }
}
