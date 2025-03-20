package mathe3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task33 {

    private static final List<Point> points = Stream.of(
//            new Point(0, -5),
//            new Point(1, 2),
//            new Point(2, 3),
//            new Point(3, 0),
//            new Point(4, 1),
//            new Point(5, -5)
            new Point(-2, 10),
            new Point(2, -30),
            new Point(4, 10),
            new Point(10, -5),
            new Point(12, 20),
            new Point(14, 20),
            new Point(30, 0)
    ).sorted(Comparator.comparing(Point::getX))
            .collect(Collectors.toList());

    private static final double slopeY0 = -2;
    private static final double slopeYN = -5;

    public static final int N_PLOT_POINTS = 100000; // number of points for plotting the graph
    public static final double PLOT_PADDING = 0.5 * 10;  // padding for the graph

    public static void main(String[] args) {
        List<Double> result = new InterpolationCalculator(points, slopeY0, slopeYN)
                .calculateSlopesAndPlot(N_PLOT_POINTS, PLOT_PADDING);
        System.out.println(result);
    }

}
