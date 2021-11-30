package mathe3;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task42 {

    // Newton-Cotes Formulas for comparison
    public static double[][] ncfs =  {
            {1./2, 1./2},
            {1./6, 2./3, 1./6},
            {1./8, 3./8, 3./8, 1./8},
            {7./90, 16./45, 2./15, 16./45, 7./90},
            {19./288, 25./96, 25./144, 25./144, 25./96, 19./288},
            {41./840, 9./35, 9./280, 34./105, 9./280, 9./35, 41./840},
            {751./17280, 3577./17280, 49./640, 2989./17280, 2989./17280, 49./640, 3577./17280, 751./17280},
            {989./28350, 2944./14175, -464./14175, 5248./14175, -454./2835, 5248./14175, -464./14175, 2944./14175, 989./28350},
            {2857./89600, 15741./89600, 27./2240, 1209./5600, 2889./44800, 2889./44800, 1209./5600, 27./2240, 15741./89600, 2857./89600},
            {16067./598752 , 26575./149688 , -16175./199584 , 5675./12474 , -4825./11088 , 17807./24948 , -4825./11088 , 5675./12474 , -16175./199584 , 26575./149688 , 16067./598752}
    };

    public static final double START = 0;
    public static final double END = 1;

    public static final int N = 15;     // max degree
    public static final int FACTOR_M = 1000; // m = n * FACTOR_M - number of points to be used in the lagrange polynomial integration


    public static void main(String[] args) {
        for (int i = 1; i <= N; i++)
            calculateWeightsForN(i);
    }

    private static void calculateWeightsForN(int n) {
        System.out.println("Number of points: " + (n +1));
        if (ncfs.length > n-1 && n >0)
            System.out.printf("Newton-Cotes-Formula for n=%d:%n%s%n", n, Arrays.toString(ncfs[n-1]));

        // generated n+1 points with constant distance
        List<Double> xs1 = xWithConstantDistance(n);
        List<Double> weights1 = new WeightsCalculator(xs1).calculateWeights(START, END, n * FACTOR_M);
        System.out.println("Xi with constant distance:\n"+weights1);

        // generated n+1 points from function
        List<Double> xs2 = xFromFunction(n, i -> (1 - Math.cos(i * Math.PI / n)) / 2);
        List<Double> weights2 = new WeightsCalculator(xs2).calculateWeights(START, END, n * FACTOR_M);
        System.out.println("Xi from function:\n"+weights2);

        System.out.println("\n---------------------\n");
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
