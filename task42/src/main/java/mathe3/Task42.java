package mathe3;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task42 {
    // Newton-Cotes Formulas
    public static double[][] ncfs =  {
            {1./2, 1./2}
    };

    public static final double START = 0;
    public static final double END = 1;

    public static final int N = 10;     // max degree

    public static void main(String[] args) {
        for (int i = 1; i <= N; i++) {
            calculateWeightsForN(i);
        }

    }

    private static void calculateWeightsForN(int n) {
        int m = n*1000; // number of points to be used in the lagrange polynomial integration
        System.out.println("Number of points: " + (n +1));
        if (ncfs.length > n-1 && n >0) System.out.printf("Newton-Cotes-Formula for %d:%n%s%n", n, Arrays.toString(ncfs[n-1]));

        // generated n+1 points with constant distance
        List<Double> xs1 = xWithConstantDistance(n);
        List<Double> weights1 = new WeightsCalculator(xs1).calculateWeights(START, END, m);
        System.out.print("Constant distance:\n");
        System.out.println(weights1);

        // generated n+1 points from function
        List<Double> xs2 = xFromFunction(n, i -> (1 - Math.cos(i * Math.PI / n)) / 2);
        List<Double> weights2 = new WeightsCalculator(xs2).calculateWeights(START, END, m);
        System.out.print("1-cos(i*pi/n)/2:\n");
        System.out.println(weights2);

        System.out.println("---------------------\n\n");
    }

    public static List<Double> xWithConstantDistance(int n){
        double step = (END - START) / n;
        return Stream.iterate(START, x -> Math.min(END, x + step))
        		.limit(n + 1)
        		.collect(Collectors.toList());
    }

    public static List<Double> xFromFunction(int n, IntToDoubleFunction nToXFunction){
        return IntStream.range(0, n+1).mapToDouble(nToXFunction).boxed().collect(Collectors.toList());
    }
}
