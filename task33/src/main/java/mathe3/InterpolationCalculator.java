package mathe3;


import Jama.Matrix;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InterpolationCalculator {

    private final List<Point> points;
    private final double slopeY0;
    private final double slopeYN;
    private final int n;                           // number of splines
    private final int nPoints;                     // n+1 - points
    private final int nEquations;                  // n-1 - equations (number of slopes to find (y1...y(n-1))

    public InterpolationCalculator(List<Point> points, double slopeY0, double slopeYN) {
        this.points = points;
        this.slopeY0 = slopeY0;
        this.slopeYN = slopeYN;
        this.n = points.size() - 1;
        this.nPoints = n + 1;
        this.nEquations = n - 1;
    }

    public List<Double> calculateSlopes(){

        double[][] matrix = new double[nEquations][nEquations];
        double[][] rightSide = new double[1][nEquations];
        for (int i = 0; i < nEquations; i++) {
            double[] row = createRow(i);
            System.arraycopy(row, 0, matrix[i], 0, nEquations); // copy first coefficients
            rightSide[i][0] = row[nEquations];                     // copy right side
        }
        Matrix M = new Matrix(matrix);
        Matrix R = new Matrix(rightSide);
        Matrix result = M.solve(R);

        double[] slopes = result.getColumnPackedCopy();

        Plotter plotter = new Plotter(-5, 5, 1000).points(points);
        List<Double> allSlopes = Arrays.stream(slopes).boxed().collect(Collectors.toList());
        allSlopes.add(0, slopeY0);
        allSlopes.add(slopeYN);

        for (int i = 0; i < n; i++) {
            plotter.function(p(i, allSlopes));
        }
        return allSlopes;
    }

    private double[] createRow(int i){
        // Copy, shift and truncate the coefficients
        double[] coeffs = {a(i), b(i), c(i)};
        double[] shiftedCoeffs = new double[nEquations+2];
        double[] truncatedCoeffs = new double[nEquations];
        Arrays.fill(shiftedCoeffs, 0);
        System.arraycopy(coeffs, 0, shiftedCoeffs, i, coeffs.length); // shift on i position
        System.arraycopy(shiftedCoeffs, 1, truncatedCoeffs, 0, truncatedCoeffs.length); // remove first and last

        // Calculate right side depending on the current equation number
        double rightSide = r(i);
        if (i == 0){ // first equation
            rightSide -= coeffs[0] * slopeY0; // move first coefficient to the right side
        }
        if (i == nEquations - 1){ // last equation
            rightSide -= coeffs[2] * slopeYN; // move last coefficient to the right side
        }

        // add right side value to the end of the array
        double[] resultArray = Arrays.copyOf(truncatedCoeffs, truncatedCoeffs.length+1);
        resultArray[truncatedCoeffs.length] = rightSide;
        return resultArray;
    }

    // MATRIX
    private double a(int i){
        return 2 / (x(i+1) - x(i));

    }
    private double b(int i){
        return 4 * (1/(x(i+1)-x(i)) + 1/(x(i+2)-x(i+1)));
    }
    private double c(int i){
        return 2/(x(i+2)-x(i+1));
    }

    private double r(int i){
        double p1 = -6 * y(i) / sq(x(i + 1) - x(i));
        double p2 = 6 * y(i+1) / sq(x(i + 1) - x(i));
        double p3 = -6 * y(i+1) / sq(x(i + 2) - x(i + 1));
        double p4 = 6 * y(i+2) / sq(x(i + 2) - x(i + 1));
        return p1 + p2 + p3 + p4;
    }
    // POLYNOMIALS
    private DoubleUnaryOperator p(int i, List<Double> slopes){
        double yPrimeI0 = slopes.get(i);
        double yPrimeI1 = slopes.get(i+1);
        DoubleUnaryOperator tConversion = t(i);

        return x ->{
            double t = tConversion.applyAsDouble(x);
            return y(i) * alpha1(t) + y(i + 1) * alpha2(t)
                    + (x(i+1) - x(i)) * (yPrimeI0 * alpha3(t) + yPrimeI1 * alpha4(t));
        };
    }

    private DoubleUnaryOperator t(int i){
        return x -> (x - x(i)) / (x(i + 1) - x(i));
    }

    private double alpha1(double t){
        return 1 - 3 * sq(t) + 2 * cb(t);
    }

    private double alpha2(double t){
        return 3 * sq(t) - 2 * cb(t);
    }

    private double alpha3(double t){
        return t - 2 * sq(t) + cb(t);
    }

    private double alpha4(double t){
        return - sq(t) + cb(t);
    }

    // COMMON
    private double x(int i){
        return points.get(i).x;
    }

    private double y(int i){
        return points.get(i).y;
    }

    private static double sq(double val){
        return val * val;
    }

    private static double cb(double val){
        return val * val * val;
    }
}
