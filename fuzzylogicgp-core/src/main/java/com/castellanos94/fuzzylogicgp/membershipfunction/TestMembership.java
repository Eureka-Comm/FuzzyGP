package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.List;

public class TestMembership {
    public static void main(String[] args) {
        NSigmoid nSigmoid = new NSigmoid(3, 6);
        MembershipFunction function = new Trapezoidal(3., 5., 7., 10.);
        List<Point> points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));

        function = new RTrapezoidal(3., 7.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new LTrapezoidal(3., 7.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new Triangular(1., 5., 9.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new Gaussian(5., 2.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new PSeudoExp(5., 2.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new Sigmoid(5., 1.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new NSigmoid(1., 5.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));

        function = new NSigmoid(3., 6.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new Sigmoid(.1, 0.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
        function = new ZForm(2., 8.);
        points = function.getPoints();
        System.out.println(String.format("%s - %3d, start = %10s, end = %10s", function, points.size(),
                points.get(0), points.get(points.size() - 1)));
    }
}
