package mathe3;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task42 {
    public static final double START = 0;
    public static final double END = 1;

    public static final int N = 10;     // degree
    public static final int M = N*1000;  // number of points for integrating lagrange polynomial

    public static double[] ncf =  {16067./598752. , 26575./149688. ,
    		-16175./199584. , 
    		5675./12474. , 
    		-4825./11088. , 
    		17807./24948. , 
    		-4825./11088. , 
    		5675./12474. ,
    		-16175./199584. ,
    		26575./149688. ,
    		16067./598752.};
    
    public static void main(String[] args) {
        System.out.println("Number of points: " + N+1);
        System.out.printf("Newton-Cotes-Formula for %d: %s%n", N, Arrays.toString(ncf));

        // generated n+1 points with constant distance
        List<Double> xs1 = xWithConstantDistance(N);
        List<Double> weights1 = new WeightsCalculator(xs1).calculateWeights(START, END, M);
        System.out.println(weights1);

        // generated n+1 points from function
        List<Double> xs2 = xFromFunction(N, i -> (1 - Math.cos(i * Math.PI / N)) / 2);
        List<Double> weights2 = new WeightsCalculator(xs2).calculateWeights(START, END, M);
        System.out.println(weights2);
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
