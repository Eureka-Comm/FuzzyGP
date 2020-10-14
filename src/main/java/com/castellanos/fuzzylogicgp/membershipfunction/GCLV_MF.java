package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;

import com.castellanos.fuzzylogicgp.logic.ACF_Logic;
import com.castellanos.fuzzylogicgp.logic.Logic;
import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.Column;

/**
 * @author s210
 */

public class GCLV_MF extends MembershipFunction {
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
        return!(L == null || beta == null || gamma == null || m == null);
    }
    public GCLV_MF(String L, String gamma, String beta, String m){
        this.L = Double.parseDouble(L);
        this.gamma = Double.parseDouble(gamma);
        this.beta = Double.parseDouble(beta);
        this.m = Double.parseDouble(m);
        this.setType(MembershipFunctionType.GCLV);
    }

    public GCLV_MF(Double L, Double gamma, Double beta, Double m) {
        this.L = L;
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
        this.setType(MembershipFunctionType.GCLV);
    }

    public GCLV_MF() {
        this.setType(MembershipFunctionType.GCLV);
    }
    @Override
    public double evaluate(Number v) {
        ACF_Logic logic; 
        for (Logic logic2 : Logic.getInstances()) {
            if(logic2 instanceof ACF_Logic ){
                logic = (ACF_Logic) logic2;
                break;
            }
        }
        
        return super.evaluate(v);
    }

    public Double getL(){
        return L;
    }

    public void setL(Double L){
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
        DoubleColumn column = DoubleColumn.create("y column");
        for (double i = 0; i < gamma*2; i+=0.1) {
            column.append(evaluate(i));
        }
        return null;
    }

    @Override
    public Column xPoints() {
        DoubleColumn column = DoubleColumn.create("x column");
        for (double i = 0; i < gamma*2; i+=0.1) {
            column.append(i);
        }
        return column;
    }




    

    
}