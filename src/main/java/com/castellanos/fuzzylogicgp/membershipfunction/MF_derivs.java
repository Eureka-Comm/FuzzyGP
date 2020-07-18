package com.castellanos.fuzzylogicgp.membershipfunction;

import java.util.HashMap;
import java.util.List;

import static java.lang.Math.*;


import com.castellanos.fuzzylogicgp.base.StateNode;


public class MF_derivs {
    
    static private Double GAUSS(double value,double sigma, double mean, String partial_parameter){
        if(partial_parameter.equals("sigma"))
            return  (2./pow(sigma,3)) * exp(-((pow(value-mean,2))/pow(sigma,2)))*pow(value-mean,2);
        else if (partial_parameter.equals("mean"))
            return (2./pow(sigma,2)) * exp(-((pow(value-mean,2))/pow(sigma,2)))*(value-mean);
        else return 0.0;

    }

    static private Double GBELL(double value, double a, double b, double c, String partial_parameter){
        if (partial_parameter.equals("a"))
            return (2. * b * pow((c-value),2) * pow(abs((c-value)/a), ((2 * b) - 2))) / (pow(a, 3) * pow((pow(abs((c-value)/a),(2*b)) + 1), 2));
        else if (partial_parameter.equals("b"))
            return -1 * (2 * pow(abs((c-value)/a), (2 * b)) * log(abs((c-value)/a))) /(pow((pow(abs((c-value)/a), (2 * b)) + 1), 2));
        else if (partial_parameter.equals("c"))
            return (2. * b * (c-value) * pow(abs((c-value)/a), ((2 * b) - 2))) / (pow(a, 2) * pow((pow(abs((c-value)/a),(2*b)) + 1), 2));
        else return 0.0;
    }

    public double partial_dMF(double value, StateNode mf_definition, String partial_parameter){
        MembershipFunctionType mf_name = mf_definition.getMembershipFunction().getType();
        switch(mf_name){
            case FPG:
                return 0.0;
            case NSIGMOID:
                return 0.0;
            case SIGMOID:
                return 0.0
            case SINGLETON:
                return 0.0;
            case MAPNOMIAL:
                return 0.0;
            case GAUSSIAN:
                return GAUSS(value, mf_definition.center, mf_definition.deviation, partial_parameter)
            case SFORM:
                return 0.0;
            case ZFORM:
                return 0.0;
            case TRAPEZOIDAL:
                return 0.0;
            case TRIANGULAR:
                return 0.0;
            case NOMINAL:
                return 0.0;
            case GAMMA:
                return 0.0;
            case LGAMMA:
                return 0.0;
            case LTRAPEZOIDAL:
                return 0.0;
            case RTRAPEZOIDAL:
                return 0.0;
            case PSEUDOEXP:
                return 0.0;
            default:
                return 0.0;
        } 
        return 0.0;
    }
}