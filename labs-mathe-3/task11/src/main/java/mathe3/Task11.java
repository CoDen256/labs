package mathe3;


import java.math.BigDecimal;
import java.util.stream.Stream;

public class Task11 {

    public static final int COUNT = 50;
    public static final int START = 2;
    public static final int FACTOR = 2;

    public static void main(String[] args) {
        generateInc(START, FACTOR)
                .limit(COUNT)
                .forEach(Task11::computeAndPrint);
        System.out.println();
        generateDec(START, FACTOR)
                .limit(COUNT)
                .forEach(Task11::computeAndPrint);
    }

    private static void computeAndPrint(float m) {
        float x0 = computePositive0(m);
        float x1 = computePositive1(m);
        float error = computeRelativeError(x0, x1);

        print("m", m);
        print("x0", x0);
        print("x1", x1);
        print("e%", error);
        System.out.println();
    }

    private static void print(String name, float number){
        System.out.printf("%s=%s%n", name, BigDecimal.valueOf(number).toPlainString());
    }

    private static Stream<Float> generateInc(float start, float factor){
        return Stream.iterate(start, s -> s * factor);
    }

    private static Stream<Float> generateDec(float start, float factor){
        return Stream.iterate(start, s -> s / factor);
    }

    private static float computeRelativeError(float x0, float x1){
        return (x1-x0)/x1*100;
    }

    private static float computePositive0(float m){
        float p = 4.0f * m;
        float q = -1.0f;

        return -p / 2.0f + sqrt(sq(p) / 4.0f - q);
    }

    private static float computePositive1(float m){
        return 1 / (sqrt(4 * sq(m) + 1) + 2 * m);
    }

    private static float sq(float i){
        return i * i;
    }
    private static float sqrt(float i){
        return (float) Math.sqrt(i);
    }
}