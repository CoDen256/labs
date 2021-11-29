package mathe3;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mathe3.plot.Plotter;
import mathe3.plot.Point;

public class Task42 {
    public static final double START = 0;
    public static final double END = 1;

    public static final int N = 600;

    public static void main(String[] args) {
        List<Double> xs0 = generateXWithConstantDistance(N);
        List<Double> ys0 = generateY(xs0, x -> Math.sin(x));
        double integral = new IntegralCalculator(xs0, ys0).integrate(START, END);
        System.out.println(integral);

        List<Double> xs = generateXFromValues(1, 2, 3, 4, 5);
        List<Double> ys = generateXFromValues(1, 2, 3, 0, -3);

        List<Point> pts = IntStream.range(0, xs.size()).mapToObj(i -> new Point(xs.get(i), ys.get(i))).collect(Collectors.toList());
        IntegralCalculator integralCalculator = new IntegralCalculator(xs, ys);
        new Plotter(pts, 100000, 0.5)
                .points(pts)
                .function(new LagrangeCalculator(xs, ys).lagrangePolynom())
                .save();

//        System.out.println(integral);
    }

    public static List<Double> generateXWithConstantDistance(int n){
        double step = (END - START) / n;
        return Stream.iterate(START, x -> x + step).limit(n + 1).collect(Collectors.toList());
    }

    public static List<Double> generateXFromValues(double... xs){
        return Arrays.stream(xs).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateX(int n, IntToDoubleFunction xFunction){
        return IntStream.range(0, n+1).mapToDouble(xFunction).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateYFromValues(double...ys){
        return Arrays.stream(ys).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateY(List<Double> xs, DoubleUnaryOperator function){
        return xs.stream().map(function::applyAsDouble).collect(Collectors.toList());
    }

}
