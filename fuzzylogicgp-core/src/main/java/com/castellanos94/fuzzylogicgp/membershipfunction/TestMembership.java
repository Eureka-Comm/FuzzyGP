package com.castellanos94.fuzzylogicgp.membershipfunction;

public class TestMembership {
    public static void main(String[] args) {
        NSigmoid nSigmoid = new NSigmoid(3,6);
        System.out.println(nSigmoid + " "+nSigmoid.getPoints().size());
        //points.forEach(p->{System.out.println(p.getX()+", "+p.getY());});
        Sigmoid sigmoid = new Sigmoid(5, 1);
        System.out.println(sigmoid+" "+sigmoid.getPoints().size());
        Triangular triangular = new Triangular(0, 1, 1);
        System.out.println(triangular+" "+triangular.getPoints().size());
        FPG fpg = new FPG(0.9818116925215841, 0.9832002976895029, 0.3463305624104552, false);
        System.out.println(fpg+ " "+fpg.getPoints().size());
        Trapezoidal trapezoidal  = new Trapezoidal(3., 5., 7., 10.);
        System.out.println(trapezoidal + " "+ trapezoidal.getPoints().size());
        LTrapezoidal lTrapezoidal = new LTrapezoidal(3, 7);
        System.out.println(lTrapezoidal+ " "+lTrapezoidal.getPoints().size());
        PSeudoExp pSeudoExp = new PSeudoExp(5., 2.);
        System.out.println(pSeudoExp+ " "+pSeudoExp.getPoints().size());
    }
}
