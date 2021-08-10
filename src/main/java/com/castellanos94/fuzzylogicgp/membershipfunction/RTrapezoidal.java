package com.castellanos94.fuzzylogicgp.membershipfunction;

public class RTrapezoidal extends LTrapezoidal {
    /**
     *
     */
    private static final long serialVersionUID = -9043056663539062607L;

    public RTrapezoidal(Double a, Double b) {
        super(a, b);
        this.type = MembershipFunctionType.RTRAPEZOIDAL;
    }

    public RTrapezoidal(String a, String b) {
        super(a, b);
        this.type = MembershipFunctionType.RTRAPEZOIDAL;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if (v < a)
            return 1.0;
        if (a <= v && v <= b) {
            double lw = (b == a) ? Double.NaN : b - a;
            return 1 - (v - a) / lw;
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("[%s %f %f]", this.type.toString(), this.a, this.b);

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
        RTrapezoidal other = (RTrapezoidal) obj;
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
        return new RTrapezoidal(a, b);
    }
}