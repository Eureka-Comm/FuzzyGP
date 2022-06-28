package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @author s210
 */

public class GCLV extends MembershipFunction {
    private static final long serialVersionUID = -2109037051439575667L; // reemplazar por el UID
    @Expose
    private Double L;
    @Expose
    private Double gamma;
    @Expose
    private Double beta;
    @Expose
    private Double m;

    @Override
    public boolean isValid() {
        return !(L == null || beta == null || gamma == null || m == null);
    }

    public GCLV(String L, String gamma, String beta, String m) {
        this(Double.parseDouble(L), Double.parseDouble(gamma), Double.parseDouble(beta), Double.parseDouble(m));
    }

    public GCLV(Double L, Double gamma, Double beta, Double m) {
        this();
        this.L = L;
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
    }

    public GCLV() {
        super(MembershipFunctionType.GCLV);
    }

    public Double getL() {
        return L;
    }

    public void setL(Double L) {
        this.L = L;
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
    public String toString() {
        return "GCLV " + this.L + " " + this.gamma + " " + this.beta + " " + this.m;
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
        GCLV other = (GCLV) obj;
        if (L == null) {
            if (other.L != null)
                return false;
        } else if (!L.equals(other.L))
            return false;
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public MembershipFunction copy() {
        return new GCLV(L, gamma, beta, m);
    }

    @Override
    public double evaluate(Number v) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Double[] toArray() {
        return new Double[] { L, gamma, beta, m };
    }
}