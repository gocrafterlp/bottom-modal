package sk.gocrafterlp.bottommodal;

public class NumbersUtil {

    public static int animate(int start, int end, float fraction) {
        if (start == end)
            return start;

        if (start > end) {
            int difference = start - end;
            int s = (int) (fraction * difference);
            return Math.max(start - s, end);
        } else {
            int difference = end - start;
            int s = (int) (fraction * difference);
            return Math.min(start + s, end);
        }
    }

    public static float animate(float start, float end, float fraction) {
        if (start == end)
            return start;

        if (start > end) {
            float difference = start - end;
            float s = (fraction * difference);
            return Math.max(start - s, end);
        } else {
            float difference = end - start;
            float s = (fraction * difference);
            return Math.min(start + s, end);
        }
    }

    public static long fractionLong(long a, Fraction fraction) {
        return fraction(a, (long) fraction.getNumerator(), (long) fraction.getDenominator());
    }

    public static double fractionDouble(long a, Fraction fraction) {
        return fraction(a, fraction.getNumerator(), fraction.getDenominator());
    }

    public static long fraction(long a, long numerator, long denominator) {
        if (denominator == 0)
            throw new ArithmeticException("Division by zero");

        return (a / denominator) * numerator;
    }

    public static double fraction(double a, double numerator, double denominator) {
        if (denominator == 0)
            throw new ArithmeticException("Division by zero");

        return (a / denominator) * numerator;
    }
}