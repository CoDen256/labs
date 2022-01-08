package mathe3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Task61 {
    // INPUT
    private static final double[] START = new double[]{
            1.0, 2.3, 4.5, 5.6
    };

    private static final int N = 4; // number of vectors to compute

    // GIVEN
    private static final double[][] A = {
            {17, 3, 2, -3},
            {4, 13, 1, 6},
            {5, -3, -19, 2},
            {1, 4, 5, 11}
    };

    private static final double[] B = {3, -2, 7, 9};
    private static final int M = START.length; // matrix dimension

    private static final List<List<Double>> vectors = new ArrayList<>(); // computed vectors

    public static void main(String[] args) {
        // add start vector to all vectors
        vectors.add(Arrays.stream(START).boxed().collect(Collectors.toList()));

        for (int i = 1; i < N+1; i++) { // number of vectors
            List<Double> vectorsI = new ArrayList<>(); // vector for i iteration
            for (int k = 0; k < M; k++) {
                vectorsI.add(computeNextX(i, k)); // xk(i)
            }
            vectors.add(vectorsI);
        }
        vectors.forEach(System.out::println);
    }

    // xk (i)
    private static double computeNextX(int i, int k) {
        double sum = 0;
        for (int j = 0; j < M; j++) {
            if (j == k) continue;
            sum += a(k, j) * x(i-1, j); // xj(i-1)
        }
        return -1 / a(k, k) * (-b(k) + sum);
    }

    // computed xk for i iteration
    private static double x(int i, int k){
        return vectors.get(i).get(k);
    }

    private static double a(int i, int j){
        return A[i][j];
    }

    private static double b(int i){
        return B[i];
    }
}
