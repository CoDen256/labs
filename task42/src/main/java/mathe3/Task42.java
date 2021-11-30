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

    public static final int N = 10; // degree
    public static final DoubleUnaryOperator fx = x -> x * x;

    public static void main(String[] args) {
        System.out.println(N);
        List<Double> xs = xWithConstantDistance(N);
        List<Double> ys = yFromFunction(xs, fx);
        integrate(xs, ys, 0);

        List<Double> xs2 = xFromFunction(N, i -> (1 - Math.cos(i * Math.PI / N)) / 2).stream().sorted().collect(Collectors.toList());
        List<Double> ys2 = yFromFunction(xs2, fx);
        integrate(xs2, ys2, 1);
    }


    public static void integrate(List<Double> xs, List<Double> ys, int s){
        List<Point> pts = IntStream.range(0, xs.size()).mapToObj(i -> new Point(xs.get(i), ys.get(i))).collect(Collectors.toList());
        double integral = new IntegralCalculator(xs, ys).integrate(START, END);
        System.out.println(integral);

        new Plotter(pts, 50000, 0.001)
                .function(new LagrangeCalculator(xs, ys).lagrangePolynomial())
                .points(pts)
                .save(s);
    }

    public static List<Double> xWithConstantDistance(int n){
        double step = (END - START) / n;
        return Stream.iterate(START, x -> Math.min(END, x + step)).limit(n + 1).collect(Collectors.toList());
    }

    public static List<Double> xFromValues(double... xs){
        return Arrays.stream(xs).boxed().collect(Collectors.toList());
    }

    public static List<Double> xFromFunction(int n, IntToDoubleFunction nToXFunction){
        return IntStream.range(0, n+1).mapToDouble(nToXFunction).boxed().collect(Collectors.toList());
    }

    public static List<Double> yFromValues(double...ys){
        return Arrays.stream(ys).boxed().collect(Collectors.toList());
    }

    public static List<Double> yFromFunction(List<Double> xs, DoubleUnaryOperator function){
        return xs.stream().map(function::applyAsDouble).collect(Collectors.toList());
    }

}
