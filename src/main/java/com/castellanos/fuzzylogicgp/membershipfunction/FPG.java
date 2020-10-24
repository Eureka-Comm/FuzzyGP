/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hp
 */
public class FPG extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -7249037051439575667L;
    @Expose
    private Double gamma;
    @Expose
    private Double beta;
    @Expose
    private Double m;

    public FPG(String beta, String gamma, String m) {
        this();
        this.gamma = Double.parseDouble(gamma);
        this.beta = Double.parseDouble(beta);
        this.m = Double.parseDouble(m);
    }

    public FPG(double beta, double gamma, double m) {
        this();
        this.beta = beta;
        this.gamma = gamma;
        this.m = m;
    }

    public FPG() {
        super(MembershipFunctionType.FPG);
    }

    @Override
    public String toString() {
        return "[FPG " + this.gamma + " " + this.beta + " " + this.m + "]";
    }

    @Override
    public double evaluate(Number v) {

        double sigm, sigmm, M;
        sigm = pow(new Sigmoid(gamma, beta).evaluate(v), m);
        sigmm = pow(1.0 - new Sigmoid(gamma, beta).evaluate(v), 1.0 - m);
        M = pow(m, m) * pow((1 - m), (1 - m));

        return ((sigm * sigmm) / M);
    }

    public Double getGamma() {
        return gamma;
    }

    public void setGamma(Double gamma) {
        this.gamma = gamma;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getM() {
        return m;
    }

    public void setM(Double m) {
        this.m = m;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beta == null) ? 0 : beta.hashCode());
        result = prime * result + ((gamma == null) ? 0 : gamma.hashCode());
        result = prime * result + ((m == null) ? 0 : m.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FPG other = (FPG) obj;
        if (beta == null) {
            if (other.beta != null)
                return false;
        } else if (!beta.equals(other.beta))
            return false;
        if (gamma == null) {
            if (other.gamma != null)
                return false;
        } else if (!gamma.equals(other.gamma))
            return false;
        if (m == null) {
            if (other.m != null)
                return false;
        } else if (!m.equals(other.m))
            return false;
        return true;
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        double step = (gamma - beta) / 50;
        double x = -gamma * 2 - beta;
        double y;
        do {
            y = evaluate(x);
            if (y > Point.EPSILON) {
                points.add(new Point(x, y));
            }
            x += step;
        } while (y <= 0.98);

        System.out.println(points.size() + " " + x);

        do {
            y = evaluate(x);
            points.add(new Point(x, y));
            x += step;
            System.out.println(new Point(x, y));
        } while (y > Point.EPSILON && x < gamma * beta);
        System.out.println(points.size());
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new FPG(beta, gamma, m);
    }

}
