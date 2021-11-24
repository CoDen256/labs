package mathe3;

import java.util.function.DoubleUnaryOperator;

public interface IntegrationFunction {
    double integrate(DoubleUnaryOperator fx, double from, double to);
}
