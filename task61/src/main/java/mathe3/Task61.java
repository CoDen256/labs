package mathe3;

public class Task61 {
    private static final double[] START = new double[]{
            1.0, 2.3, 4.5, 5.6
    };

    private static final double[][] A = {
            {17, 3, 2, -3},
            {4, 13, 1, 6},
            {5, -3, -19, 2},
            {1, 4, 5, 11}
    };

    private static final double[] B = {3, -2, 7, 9};
    private static final int M = START.length; // matrix dimension

    private static final int N = 4; // number of vectors to compute


    public static void main(String[] args) {

    }

    private static double x(int k) {
        double sum = 0;
        for (int j = 0; j < M; j++) {
            if (j == k) continue;
            sum += a(k, j) * x(j);
        }
        return -1 / a(k, k) * (-b(k) + sum);
    }

    private static double a(int i, int j){
        return A[i][j];
    }

    private static double b(int i){
        return B[i];
    }
}
