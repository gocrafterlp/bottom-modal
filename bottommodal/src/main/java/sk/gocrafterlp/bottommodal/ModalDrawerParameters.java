package sk.gocrafterlp.bottommodal;

public class ModalDrawerParameters {

    public static ModalDrawerParameters create() {
        return new ModalDrawerParameters();
    }

    boolean showing = false, animating = false, dismissible = true;
    int animationDurationLong, animationDurationShort;
    long pressDeflectionTolerance;
    Fraction dismissPoint;

    private ModalDrawerParameters() {
        clear();
    }

    void clear() {
        dismissible = true;
        animationDurationLong = 200;
        animationDurationShort = 150;
        pressDeflectionTolerance = 20;
        dismissPoint = new Fraction(1, 3);
    }

    public void inheritFrom(ModalDrawerParameters parameters) {
        this.dismissible = parameters.dismissible;
        this.animationDurationLong = parameters.animationDurationLong;
        this.animationDurationShort = parameters.animationDurationShort;
        this.pressDeflectionTolerance = parameters.pressDeflectionTolerance;
        if (parameters.dismissPoint != null) this.dismissPoint = parameters.dismissPoint;
    }

    public boolean isShowing() {
        return showing;
    }

    public boolean isAnimating() {
        return animating;
    }

    public boolean isDismissible() {
        return dismissible;
    }

    public ModalDrawerParameters setDismissible(boolean dismissible) {
        this.dismissible = dismissible;
        return this;
    }

    public int getAnimationDurationLong() {
        return animationDurationLong;
    }

    public ModalDrawerParameters setAnimationDurationLong(int animationDurationLong) {
        this.animationDurationLong = animationDurationLong;
        return this;
    }

    public int getAnimationDurationShort() {
        return animationDurationShort;
    }

    public ModalDrawerParameters setAnimationDurationShort(int animationDurationShort) {
        this.animationDurationShort = animationDurationShort;
        return this;
    }

    public long getPressDeflectionTolerance() {
        return pressDeflectionTolerance;
    }

    public ModalDrawerParameters setPressDeflectionTolerance(long pressDeflectionTolerance) {
        this.pressDeflectionTolerance = pressDeflectionTolerance;
        return this;
    }

    public Fraction getDismissPoint() {
        return dismissPoint != null ? dismissPoint : Fraction.ONE;
    }

    public ModalDrawerParameters setDismissPoint(Fraction dismissPoint) {
        this.dismissPoint = dismissPoint;
        return this;
    }
}