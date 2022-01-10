/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

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

    public FPG(double beta, double gamma, double m, boolean editable) {
        this(beta, gamma, m);
        this.editable = editable;
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
        double step = Math.abs(gamma - beta) / 100.0;
        if (step < 0.0001) {
            step = 0.001;
        }
        double y, max = 0;
        double x = gamma - 3 * gamma / 2.0;
        while (x <= (gamma + 3 * gamma / 2.0) && points.size() < 5000) {
            y = evaluate(x);
            if (max < y)
                max = y;
            points.add(new Point(x, y));
            x += step;
        }
        ArrayList<Point> tmp = new ArrayList<>();
        int indexFirstOne = -1, indexLastZero = -1;
        int secondZero = -1;
        for (int i = 0; i < points.size() - 1; i++) {
            Point p = points.get(i);
            if (Math.abs(p.getY() - 1.0) < 0.00001 && indexFirstOne == -1) {
                indexFirstOne = i;
            }
            if (p.getY() > 0.00001 && indexLastZero == -1) {
                indexLastZero = i;
            }
            if (indexLastZero != -1 && secondZero == -1 && p.getY() <= 0.00001) {
                secondZero = i;
            }
        }
        if (indexLastZero != -1 || secondZero != -1) {
            if (secondZero != -1 && secondZero + 5 < points.size()) {
                secondZero += 5;
            } else {
                secondZero = points.size();
            }
            for (int i = (indexLastZero - 5 > 0) ? indexLastZero - 1 : indexLastZero; i < secondZero; i++) {
                tmp.add(points.get(i));
            }
            return tmp;
        }
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new FPG(beta, gamma, m, editable);
    }

    @Override
    public boolean isValid() {
        return (beta != null && gamma != null && m != null) && (m >= 0 && m <= 1.0) && (gamma > beta);
    }

}
