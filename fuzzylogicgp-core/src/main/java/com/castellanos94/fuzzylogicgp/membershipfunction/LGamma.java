package com.castellanos94.fuzzylogicgp.membershipfunction;

public class LGamma extends Gamma {
    /**
     *
     */
    private static final long serialVersionUID = -7381927880504229507L;

    @Override
    public boolean isValid() {
        return !(a == null || b == null);
    }

    public LGamma(double a, double b) {
        super(a, b);
        this.type = MembershipFunctionType.LGAMMA;
    }

    public LGamma(String a, String b) {
        super(a, b);
        this.setType(MembershipFunctionType.LGAMMA);
    }


    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 0.0;
        return (b * Math.pow(v - a, 2) / (1 + b * Math.pow(v - a, 2)));
    }

    @Override
    public String toString() {
        return String.format("[%s %f %f]", this.type.toString(), this.a, this.b);
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
        LGamma other = (LGamma) obj;
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
    public MembershipFunction copy() {
        return new LGamma(a, b);
    }

}