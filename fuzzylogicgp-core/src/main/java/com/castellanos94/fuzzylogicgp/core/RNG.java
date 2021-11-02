package com.castellanos94.fuzzylogicgp.core;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random Number Generator, using java implementation
 * 
 * @see Random
 * @see ThreadLocalRandom
 */
public class RNG {
    private ThreadLocalRandom localRandom;
    private Random random;

    /**
     * Thread Local Random with seed
     * 
     * @param seed control
     * @return RNG
     * @see Random
     */
    public static RNG getRNG(final long seed) {
        return new ThreadLocal<RNG>() {
            @Override
            protected RNG initialValue() {
                return new RNG(seed);
            }
        }.get();
    }

    /**
     * This method is recommended when a controlled seed is not required.
     * 
     * @return RNG instance
     * @see ThreadLocalRandom
     */
    public static RNG getRNG() {
        return new RNG();
    }

    private RNG(long seed) {
        this.random = new Random(seed);
    }

    private RNG() {
        localRandom = ThreadLocalRandom.current();
    }

    /**
     * Java random number generator, avoid assigning seed manually.
     * 
     * @exception UnsupportedOperationException
     * @return rng
     */
    public Random getRandom() {
        return random == null ? localRandom : random;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive. The difference
     * between min and max can be at most <code>Integer.MAX_VALUE - 1</code>.
     * <p>
     * This method uses the random number generator with which it was initialized.
     * </p>
     * 
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     * @see java.util.concurrent.ThreadLocalRandom#nextInt(int)
     */
    public int randInt(int min, int max) {
        Random r = random == null ? localRandom : random;
        return r.nextInt((max - min) + 1) + min;
    }

}
