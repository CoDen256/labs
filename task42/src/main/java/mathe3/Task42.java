package mathe3;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task42 {
    public static final double START = 0;
    public static final double END = 1;

    public static final int N = 4;

    public static void main(String[] args) {
        List<Double> xs = generateXWithConstantDistance(N);
        List<Double> ys = generateY(xs, x -> x * x);
        double integral = new IntegralCalculator(xs, ys).calculate();
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
