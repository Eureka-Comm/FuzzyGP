/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public abstract class MembershipFunction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9006368296289781684L;
    protected boolean editable;

    public MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public MembershipFunction() {
        this(null);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    @Expose
    public MembershipFunctionType type;

    public abstract boolean isValid();

    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }

    public double evaluate(Number v) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public double evaluate(String key) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public abstract List<Point2D> getPoints();

    public Double partialDerivate(double value, String partial_params) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public abstract MembershipFunction copy();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    protected ArrayList<Point2D> calculatePoints(final double step, final double start) {
        ArrayList<Point2D> points = new ArrayList<>();
        double x = start;
        double y;
        double epsilon = 0.01;
        do {
            y = evaluate(x);
            if (y > epsilon && Math.abs(y - 1.0) > epsilon) {
                Point2D p = new Point2D.Double(x, y);
                points.add(p);
            }
            x += step;
            if(x > step*100 &&  (Math.abs(y - 1.0) < epsilon  || y < epsilon)){
                break;
            }
        } while (( y > 0 && Math.abs(y - 1.0) > 0.0001) && points.size() < 500  );
        if (points.isEmpty()) {
            x = start;
            do {
                y = evaluate(x);
                if (y > epsilon) {
                    Point2D p = new Point2D.Double(x, y);
                    points.add(p);
                }
                x += step;
            } while (y <= 0.98 && points.size() < 600);

            do {
                y = evaluate(x);
                Point2D p = new Point2D.Double(x, y);
                points.add(p);

                x += step;
            } while (y > epsilon && points.size() < 1200);
        }
        return points;
    }

}
