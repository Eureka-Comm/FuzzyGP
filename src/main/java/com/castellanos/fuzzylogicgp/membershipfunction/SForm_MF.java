package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

/**
 * S-shaped membership function MathWorks-based implementation
 */
public class SForm_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 7478244472813556676L;
    @Expose
    private Double a;
    @Expose
    private Double b;

    @Override
    public boolean isValid() {
        return !(a == null || b == null);
    }

    public SForm_MF(Double a, Double b) {
        this.a = a;
        this.b = b;
        this.setType(MembershipFunctionType.SFORM);
    }

    public SForm_MF(String a, String b) {
        this.a = Double.parseDouble(a);
        this.b = Double.parseDouble(b);
        this.setType(MembershipFunctionType.SFORM);
    }
    public SForm_MF(){
        this.setType(MembershipFunctionType.SFORM);
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 0;
        if (a <= v && v <= (a + b) / 2.0)
            return 2 * Math.pow((v - a) / (b - a), 2);
        if ((a + b) / 2.0 <= v && v <= b)
            return (1 - 2 * Math.pow((v - b) / (b - a), 2));

        return 1;
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < b * 2; i += 0.1) {
            xColumn.append(i);
        }
        return xColumn;
    }

    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < b * 2; i += 0.1) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
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
        SForm_MF other = (SForm_MF) obj;
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
        return new SForm_MF(a, b);
    }
}