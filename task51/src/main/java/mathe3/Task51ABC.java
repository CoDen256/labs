package mathe3;

import java.util.ArrayList;
import java.util.List;
import mathe3.plot.Plotter;
import mathe3.plot.Point;

public class Task51ABC {

    private static final double FROM = 0;
    private static final double TO = 2*Math.PI;
    public static final EulerMethod EULER_METHOD = new EulerMethod();
    public static final HeunMethod HEUN_METHOD = new HeunMethod();
    public static final Point START_CONDITION = new Point(0, 0.5);

    public static void main(String[] args) {
        double h = 0.5;
        List<Point> euler = new ArrayList<>();
        List<Point> heun = new ArrayList<>();
        for (int i = 0; i < 16; i ++ ){
            double[] doubles = calculateForH(h);
            euler.add(new Point(h, doubles[0]));
            heun.add(new Point(h, doubles[1]));
            h /= 2.;
        }

        new Plotter(euler, 50000, 0.01).save(1);
        new Plotter(heun, 50000, 0.01).save(2);
    }

    private static double[] calculateForH(double h) {
        System.out.println("\nH = "+h);
        List<Point> approxEuler = EULER_METHOD.calculatePoints(Task51ABC::yDerivative, START_CONDITION, FROM, TO, h);
        Point lastEuler = approxEuler.get(approxEuler.size() - 1);
//        System.out.printf("Euler y(2pi) = %f%n", lastEuler.y);
        List<Point> approxHeun = HEUN_METHOD.calculatePoints(Task51ABC::yDerivative, START_CONDITION, FROM, TO, h);
        Point lastHeun = approxHeun.get(approxHeun.size() - 1);
//        System.out.printf("Heun y(2pi) = %f%n", lastHeun.y);
        double actualLast = y(TO);
//        System.out.printf("Actual y(2pi) = %f%n", actualLast);

        double diff1 = Math.abs(actualLast - lastEuler.y);
        System.out.printf("Euler diff: %f%n", diff1);

        double diff2 = Math.abs(actualLast - lastHeun.y);
        System.out.printf("Heun diff: %f%n", diff2);

        return new double[]{diff1, diff2};
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
