package mathe3;


import Jama.Matrix;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import mathe3.plot.Plotter;

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

    public List<Double> calculateSlopesAndPlot(int nPoints, double padding) {
        List<Double> slopes = calculateSlopes();
        plotAndSave(slopes, nPoints, padding);
        return slopes;
    }

    private List<Double> calculateSlopes() {
        double[] slopes = createAndSolveMatrix();
        List<Double> allSlopes = Arrays.stream(slopes).boxed().collect(Collectors.toList());
        allSlopes.add(0, slopeY0); // insert first given slope as first
        allSlopes.add(slopeYN);      // insert last given slope as last
        return allSlopes;
    }

    private double[] createAndSolveMatrix() {
        double[][] matrix = new double[nEquations][nEquations];
        double[][] rightSide = new double[nEquations][1];
        for (int i = 0; i < nEquations; i++) {
            System.arraycopy(calculateCoefficientsRow(i), 0, matrix[i], 0, nEquations);
            rightSide[i] = calculateRightSideRow(i);
        }
        return solveMatrix(matrix, rightSide);
    }

    private double[] solveMatrix(double[][] matrix, double[][] rightSide) {
        Matrix M = new Matrix(matrix);
        Matrix R = new Matrix(rightSide);
        Matrix result = M.solve(R);
        return result.getColumnPackedCopy();
    }

    private double[] calculateCoefficientsRow(int i) {
        // Copy, shift and truncate the coefficients
        double[] coeffs = {a(i), b(i), c(i)};               // the coefficients for y'(i), y'(i+1) and y'(i+2) slopes
        double[] shiftedCoeffs = new double[nEquations + 2];  // the shifted coefficients for slopes (y'0...y'n)
        double[] truncatedCoeffs = new double[nEquations];  // the truncated coefficients for slopes (y'1...y'n-1)
        Arrays.fill(shiftedCoeffs, 0);
        System.arraycopy(coeffs, 0, shiftedCoeffs, i, coeffs.length); // shift on i position
        System.arraycopy(shiftedCoeffs, 1, truncatedCoeffs, 0, truncatedCoeffs.length); // remove first and last

        return truncatedCoeffs;
    }

    private double[] calculateRightSideRow(int i) {
        // Calculate right side depending on the current equation number
        double rightSide = r(i); // plain right side
        if (i == 0) { // first equation
            rightSide -= a(i) * slopeY0; // move first coefficient to the right side
        }
        if (i == nEquations - 1) { // last equation
            rightSide -= c(i) * slopeYN; // move last coefficient to the right side
        }
        return new double[]{rightSide};
    }

    private void plotAndSave(List<Double> allSlopes, int nPoints, double padding) {
        Plotter plotter = new Plotter(points, nPoints, padding);
        for (int i = 0; i < n; i++) {
            plotter.function(p(i, allSlopes), x(i), x(i + 1)); // plot function pi(x) for i slope from x(i) to x(i+1)
//            plotter.functionExceptRange(p(i, allSlopes), x(i), x(i+1)); // plot polynomial outside of the bounds
        }
        plotter.points(points) // plot base points
                .save(); // saves to result.png
    }

    // MATRIX
    // coefficient for y'(i) slope
    private double a(int i) {
        return 2 / (x(i + 1) - x(i));

    }

    // coefficient for y'(i+1) slope
    private double b(int i) {
        return 4 * (1 / (x(i + 1) - x(i)) + 1 / (x(i + 2) - x(i + 1)));
    }

    // coefficient for y'(i+2) slope
    private double c(int i) {
        return 2 / (x(i + 2) - x(i + 1));
    }

    // right side of the matrix
    private double r(int i) {
        double p1 = -6 * y(i) / sq(x(i + 1) - x(i));
        double p2 = 6 * y(i + 1) / sq(x(i + 1) - x(i));
        double p3 = -6 * y(i + 1) / sq(x(i + 2) - x(i + 1));
        double p4 = 6 * y(i + 2) / sq(x(i + 2) - x(i + 1));
        return p1 + p2 + p3 + p4;
    }

    // POLYNOMIALS
    // calculate Pi(x) function depending on i and slopes
    private DoubleUnaryOperator p(int i, List<Double> slopes) {
        double yPrimeI0 = slopes.get(i);        // slope for i
        double yPrimeI1 = slopes.get(i + 1);      // slope for i+1

        return x -> {
            double t = t(i).applyAsDouble(x); // convert x to t
            return y(i) * alpha1(t) + y(i + 1) * alpha2(t)
                    + (x(i + 1) - x(i)) * (yPrimeI0 * alpha3(t) + yPrimeI1 * alpha4(t));
        };
    }

    private DoubleUnaryOperator t(int i) {
        return x -> (x - x(i)) / (x(i + 1) - x(i));
    }

    private double alpha1(double t) {
        return 1 - 3 * sq(t) + 2 * cb(t);
    }

    private double alpha2(double t) {
        return 3 * sq(t) - 2 * cb(t);
    }

    private double alpha3(double t) {
        return t - 2 * sq(t) + cb(t);
    }

    private double alpha4(double t) {
        return -sq(t) + cb(t);
    }

    // COMMON
    private double x(int i) {
        return points.get(i).x;
    }

    private double y(int i) {
        return points.get(i).y;
    }

    private static double sq(double val) {
        return val * val;
    }

    private static double cb(double val) {
        return val * val * val;
    }
}
