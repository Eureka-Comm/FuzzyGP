package com.castellanos94.fuzzylogicgp.membershipfunction;

public class Point {
    public static Double EPSILON = 0.01;
    protected final double x;
    protected final double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
