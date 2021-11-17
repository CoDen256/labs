package mathe3;

import java.util.List;

public class Task33 {

    private static final List<Point> points = List.of(
            new Point(1, 2),
            new Point(2, 3),
            new Point(3, 0)
    );
    private static final double slopeY0 = 5;
    private static final double slopeYN = 2;


    public static void main(String[] args){
        List<Double> result = new InterpolationCalculator(points, slopeY0, slopeYN).calculateSlopes();
        System.out.println(result);
    }

}
