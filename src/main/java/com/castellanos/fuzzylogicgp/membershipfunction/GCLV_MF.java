package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;
import com.google.gson.annotations.Expose;

import tech.tablesaw.columns.Column;

/**
 * @author s210
 */

public class GCLV_MF extends MembershipFunction {
    private static final long serialVersionUID = -2109037051439575667L; // reemplazar por el UID
    @Expose
    private Integer L;
    @Expose
    private Double gamma;
    @Expose
    private Double beta;
    @Expose
    private Integer m;

    public GCLV_MF(String L, String gamma, String beta, String m){
        this.L = Integer.parseInt(L);
        this.gamma = Double.parseDouble(gamma);
        this.beta = Double.parseDouble(beta);
        this.m = Integer.parseInt(m);
        this.setType(MembershipFunctionType.GCLV);
    }

    public GCLV_MF(Integer L, Double gamma, Double beta, Integer m) {
        this.L = L;
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
        this.setType(MembershipFunctionType.GCLV);
    }

    public GCLV_MF() {
        this.setType(MembershipFunctionType.GCLV);
    }

    public Integer getL(){
        return L;
    }

    public void setL(Integer L){
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

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new GCLV_MF(L, gamma, beta, m);
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
        GCLV_MF other = (GCLV_MF) obj;
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
    public Column yPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Column xPoints() {
        // TODO Auto-generated method stub
        return null;
    }




    

    
}