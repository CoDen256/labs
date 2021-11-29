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

    public static final int N = 1000;
    public static final DoubleUnaryOperator fx = x -> Math.exp(x);

    public static void main(String[] args) {
        List<Double> xs = generateXWithConstantDistance(N);
        List<Double> ys = generateY(xs, fx);
        integrate(xs, ys, 0);

        List<Double> xs2 = generateX(N, i -> (1 - Math.cos(i * Math.PI / N)) / 2);
        List<Double> ys2 = generateY(xs2, fx);
        integrate(xs2, ys2, 1);
    }


    public static void integrate(List<Double> xs, List<Double> ys, int s){
        double integral = new IntegralCalculator(xs, ys).integrate(START, END);
        System.out.println(integral);
        List<Point> pts = IntStream.range(0, xs.size()).mapToObj(i -> new Point(xs.get(i), ys.get(i))).collect(Collectors.toList());
        new Plotter(pts, 50000, 0.001).points(pts).save(s);

    }

    public static List<Double> generateXWithConstantDistance(int n){
        double step = (END - START) / n;
        return Stream.iterate(START, x -> Math.min(END, x + step)).limit(n + 1).collect(Collectors.toList());
    }

    public static List<Double> generateXFromValues(double... xs){
        return Arrays.stream(xs).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateX(int n, IntToDoubleFunction nToXFunction){
        return IntStream.range(0, n+1).mapToDouble(nToXFunction).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateYFromValues(double...ys){
        return Arrays.stream(ys).boxed().collect(Collectors.toList());
    }

    public static List<Double> generateY(List<Double> xs, DoubleUnaryOperator function){
        return xs.stream().map(function::applyAsDouble).collect(Collectors.toList());
    }

}
