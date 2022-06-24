/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.core;

import java.util.Random;

/**
 *
 * @author hp
 */
public class Utils {

    /**
     * Returns a pseudo-random number between min and max, inclusive. The difference
     * between min and max can be at most <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(Random random, int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

}
