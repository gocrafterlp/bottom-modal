package sk.gocrafterlp.bottommodal;

public class Fraction {

    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ZERO = new Fraction(0, 1);
    public static final Fraction HALF = new Fraction(1, 2);
    public static final Fraction QUARTER = new Fraction(1, 4);

    public static Fraction of(double a, double b) {
        return new Fraction(a, b);
    }

    double numerator;
    double denominator;

    public Fraction(double numerator, double denominator) {
        if (denominator == 0)
            throw new ArithmeticException("Division by zero");
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Fraction(Fraction other) {
        this.numerator = other.numerator;
        this.denominator = other.denominator;
    }

    public double getNumerator() {
        return numerator;
    }

    public Fraction setNumerator(double numerator) {
        return new Fraction(numerator, denominator);
    }

    public double getDenominator() {
        return denominator;
    }

    public Fraction setDenominator(double denominator) {
        return new Fraction(numerator, denominator);
    }

    public double asDouble() {
        return numerator / denominator;
    }
}