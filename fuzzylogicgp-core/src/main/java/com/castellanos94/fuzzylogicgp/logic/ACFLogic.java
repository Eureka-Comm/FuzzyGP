package com.castellanos94.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * @author Pedraza, Edgar
 */

class MathOper {

    /**
     * Returns the natural logarithm (base <i>e</i>) of a {@code double} value.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN or less than zero, then the result is NaN.
     * <li>If the argument is positive infinity, then the result is
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for integer <i>n</i>,
     * then the result is <i>n</i>.
     * </ul>
     *
     * @param v1 a value
     * @return the value ln&nbsp;{@code v1}, the natural logarithm of ({@code v1} +
     *         square root {@code 1 + v1^2}).
     */
    public double asinh(double v1) {
        return log(v1 + sqrt(1 + pow(v1, 2)));
    }

    /**
     * Returns the valuo of logic of a {@code double} value. Special cases:
     * <ul>
     * <li>If the argument is 1, then the result is -1 * the natural logarithm (base
     * <i>e</i>) of v1.
     * <li>If the argument is 2, then the result is -1 * log({@code v1}^m)
     * <li>If the argument is 3, then the result is -1 *
     * {@code asinh}(log({@code v1})).
     * <li>If the argument is 4, then the result is {@code -1 * log(v1)/log(m)}.
     * </ul>
     *
     * @param v1,L,m are values
     * @return the value in other cases of 1,2,3,4; Is 0
     */
    public double f(double v1, int L, int m) {
        if (L == 1)
            return -1 * log(v1);
        else if (L == 2)
            return -1 * pow(log(v1), m);
        else if (L == 3)
            return -1 * asinh(log(v1));
        else if (L == 4)
            return -1 * (log(v1) / log(m));
        else
            return 0.0;
    }

    public double invf(double v1, int L, int m) {
        if (L == 1)
            return exp(-1 * v1);
        else if (L == 2) {
            double aux = pow(abs(v1), 1 / (double) m);
            if (v1 < 0) {
                aux *= -1;
            }
            return exp(-1 * aux);
        } else if (L == 3)
            return exp(-1 * sinh(v1));
        else if (L == 4)
            return exp(-1 * log(m) * v1);
        else
            return 0.0;

    }

    public double alfa(double betta, double gamma, int L, int m) {
        double v1 = f(0.99, L, m);
        double v2 = f(0.01, L, m);
        return ((-1 * v1) - (-1 * v2)) / (gamma - betta);
    }

    public double Sg(double v1, double betta, double gamma, int L, int m) {
        double x1 = alfa(betta, gamma, L, m) * (v1 - gamma);
        return 1 / (1 + invf(x1, L, m));
    }

    public double Hsg(double M, double v1, int L, int m) {
        if (M != 0 || !Double.isInfinite(v1))
            return invf(M * v1, L, m);
        else if (M == 0 && Double.isInfinite(v1))
            return invf(0, L, m);
        else
            return 0.0;

    }

    public double HNsg(double M, double v1, int L, int m) {
        if (m != 1 || !Double.isInfinite(v1))
            return invf((1 - M) * v1, L, m);
        else if (M == 1 && Double.isInfinite(v1))
            return invf(0, L, m);
        else
            return 0.0;
    }

    public double F(double v1, double betta, double gamma, double M, int L, int m) {
        double Sg = Sg(v1, betta, gamma, L, m);
        double Nsg = 1 - Sg;
        double Hsg = Hsg(M, f(Sg, L, m), L, m);
        double HNsg = HNsg(M, f(Nsg, L, m), L, m);
        return (invf(f(Hsg, L, m) + f(HNsg, L, m), L, m));
    }

}

public class ACFLogic extends Logic {

    private Integer L, m;
    private MathOper Mo = new MathOper();

    public ACFLogic(int L, int m) {
        this(ImplicationType.Zadeh, L, m);
    }

    public ACFLogic(ImplicationType implicationType, int L, int m) {
        super(implicationType);
        this.L = L;
        this.m = m;
    }

    @Override
    public double not(final double v1) {
        return 1 - v1;
    }

    @Override
    public double and(final double v1, final double v2) {
        return Mo.invf((Mo.f(v1, L, m) + Mo.f(v2, L, m)) / 2, L, m);
    }

    @Override
    public double and(ArrayList<Double> values) {
        return Mo.invf(values.stream().mapToDouble(a -> Mo.f(a, L, m)).sum() / values.size(), L, m);
    }

    @Override
    public double or(final double v1, final double v2) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double or(final ArrayList<Double> values) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double forAll(final List<Double> values) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double exist(final List<Double> values) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
