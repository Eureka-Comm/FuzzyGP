package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.columns.Column;
import static java.lang.Math.*;

/**
 * The class {@code GBELL_MF} is Generalized Bell function fuzzy membership generator.
 * 
 */
public class GBELL_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 1421421626L;
    @Expose 
    private Double width;
    @Expose
    private Double slope;
    @Expose
    private Double center;
    
    @Override
    public boolean isValid() {
        return !(width== null || slope== null || center ==null );
    }
    /**
     * Parameters
     * @param width Double
     * Bell function parameter controlling width. See Note for definition.
     * @param slope Double
     * Bell function parameter controlling slope. See Note for definition.
     * @param center Double
     * Bell function parameter defining the center. See Note for definition.
     */
    public GBELL_MF(Double width, Double slope, Double center){
        this.width = width;
        this.slope = slope;
        this.center = center;
        this.setType(MembershipFunctionType.GBELL);
    }
   

    /**
     * Parameters
     * @param width Double
     * Bell function parameter controlling width.
     * @param slope Double
     * Bell function parameter controlling slope. 
     * @param center Double
     * Bell function parameter defining the center.
     */
    public GBELL_MF(String width, String slope, String center){
        this.width = Double.parseDouble(width);
        this.slope = Double.parseDouble(slope);
        this.center = Double.parseDouble(center);
        this.setType(MembershipFunctionType.GBELL);
    }

    public GBELL_MF(){
        this.setType(MembershipFunctionType.GBELL);
    }

    /**
     * Returns
     * {@code y} : 1d array
     * Generalized Bell fuzzy membership function.
     *
     * Definition of Generalized Bell function is:
     * @return {@code y = 1 / (1 + abs([x - c] / a) ** [2 * b])}
     */
    @Override
    public Double partialDerivate(double value, String partial_parameter){
        if (partial_parameter.equals("width"))
            return (2. * slope * pow((center-value),2) * pow(abs((center-value)/width), ((2 * slope) - 2))) / (pow(width, 3) * pow((pow(abs((center-value)/width),(2*slope)) + 1), 2));
        else if(partial_parameter.equals("slope"))
            return -1 * (2 * pow(abs((center-value)/width), (2 * slope)) * log(abs((center-value)/width))) /(pow((pow(abs((center-value)/width), (2 * slope)) + 1), 2));
        else if (partial_parameter.equals("center"))
            return (2. * slope * (center-value) * pow(abs((center-value)/width), ((2 * slope) - 2))) /(pow(width, 2) * pow((pow(abs((center-value)/width),(2*slope)) + 1), 2));
        else return 0.0;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return 1. / (1. + pow(abs((v - center) / width),(2 * slope)));
    }

    public void setWidth(Double width){
        this.width = width;
    }
    public void setSlope(Double slope){
        this.slope = slope;
    }

    public void setCenter(Double center){
        this.center = center;
    }

    public Double getWidth(){
        return this.width;
    }

    public Double getSlope(){
        return this.slope;
    }

    public Double getCenter(){
        return this.center;
    }

    @Override
    public String toString() {
        return String.format("[%s %f, %f, %f]", this.type.toString(), this.width, this.slope, this.center);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((width == null) ? 0 : width.hashCode());
        result = prime * result + ((slope == null) ? 0 : slope.hashCode());
        result = prime * result + ((center == null) ? 0 : center.hashCode());
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
        GBELL_MF other = (GBELL_MF) obj;
        if (width == null) {
            if (other.width != null)
                return false;
        } else if (!width.equals(other.width))
            return false;
        if (slope == null) {
            if (other.slope != null)
                return false;
        } else if (!slope.equals(other.slope))
            return false;
        if (center == null) {
            if (other.center != null)
                return false;
        } else if (!center.equals(other.center))
            return false;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new GBELL_MF(width, slope, center);
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