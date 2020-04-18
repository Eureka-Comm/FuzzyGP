/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;


/**
 *
 * @author hp
 */
public class FPG extends AMembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -7249037051439575667L;
    private Double gamma;
    private Double beta;
    private Double m;

    public FPG(String beta, String gamma, String m) {
        this.gamma = Double.parseDouble(gamma);
        this.beta = Double.parseDouble(beta);
        this.m = Double.parseDouble(m);
        this.setType(MembershipFunctionType.FPG);
    }

    public FPG() {
        this.setType(MembershipFunctionType.FPG);
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public String toString() {
        return "FPG " + this.beta + " " + this.gamma + " " + this.m;
    }

    @Override
    public double evaluate(double v) {
        // BigDecimal sigm, sigmm, M;

        double sigm, sigmm, M;
          sigm = pow(new Sigmoid(gamma, beta).evaluate(v), m); 
          sigmm = pow(1.0 - new Sigmoid(gamma, beta).evaluate(v), 1.0 - m); 
          M = pow(m, m) * pow((1 - m), (1 -
          m));
          
          return ((sigm * sigmm) / M);
         
        /*
        BigDecimal one = new BigDecimal("1", MathContext.DECIMAL128);
        BigDecimal calc_sig = new Sigmoid(gamma, beta).evaluate(v);

        // Apfloat apsigm = ApfloatMath.pow(new Apfloat(calc_sig),new Apfloat(m));
        BigDecimal bgsigm = BigDecimalMath.pow(calc_sig, m, MathContext.DECIMAL128);

        // Apfloat apsigmm = ApfloatMath.pow(new Apfloat(one.subtract(calc_sig)), new
        // Apfloat(one.subtract(m)));
        BigDecimal bgsigmm = BigDecimalMath.pow(one.subtract(calc_sig), one.subtract(m), MathContext.DECIMAL128);

        // Apfloat M = ApfloatMath.pow(new Apfloat(m), new
        // Apfloat(m)).multiply(ApfloatMath.pow(new Apfloat(one.subtract(m)), new
        // Apfloat(one.subtract(m))));
        BigDecimal bgM = BigDecimalMath.pow(m, m, MathContext.DECIMAL128)
                .multiply(BigDecimalMath.pow(one.subtract(m), one.subtract(m), MathContext.DECIMAL128));
        return bgsigm.multiply(bgsigmm).divide(bgM, MathContext.DECIMAL128);*/
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
        FPG fpg = (FPG) super.clone();
        /*
        fpg.setBeta(new BigDecimal(this.getBeta().toString()));
        fpg.setGamma(new BigDecimal(this.getGamma().toString()));
        fpg.setM(new BigDecimal(this.getM().toString()));
        */
        return fpg;
    }
}
