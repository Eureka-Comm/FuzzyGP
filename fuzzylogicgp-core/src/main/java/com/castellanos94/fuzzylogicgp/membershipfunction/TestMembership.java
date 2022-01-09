package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.List;

public class TestMembership {
    public static void main(String[] args) {
        NSigmoid nSigmoid = new NSigmoid(3,6);
        System.out.println(nSigmoid + " "+nSigmoid.isValid());
        List<Point> points = nSigmoid.getPoints();
        System.out.println("Points "+points.size());
        points.forEach(p->{System.out.println(p.getX()+", "+p.getY());});
    }
}
