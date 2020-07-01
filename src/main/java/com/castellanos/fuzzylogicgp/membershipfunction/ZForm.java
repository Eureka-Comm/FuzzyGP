package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;
/**
 * Z-shaped memberhip function MathWorks-based implementation
 * 
 */
public class ZForm extends AMembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -5343964450642686416L;
    @Expose
    private Double a;
    @Expose
    private Double b;

    public ZForm(Double a, Double b) {
        this.a = a;
        this.b = b;
        this.setType(MembershipFunctionType.ZFORM);
    }

    public ZForm(String a, String b) {
        this.a = Double.parseDouble(a);
        this.b = Double.parseDouble(b);
        this.setType(MembershipFunctionType.ZFORM);
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double evaluate(double v) {
        if (v <= a)
            return 1.0;
        if (a <= v && v <= (a + b) / 2.0)
            return (1 - 2 * Math.pow((v - a) / (b - a), 2));
        if ((a + b) / 2.0 <= v && v <= b)
            return 2 * Math.pow((v - b) / (b - a), 2);
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("[%s %f, %f]", this.type.toString(), this.a, this.b);
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
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
        ZForm other = (ZForm) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        return true;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new ZForm(a, b);
    }
}