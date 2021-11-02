package com.castellanos94.fuzzylogicgp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.castellanos94.fuzzylogicgp.core.RNG;
import com.castellanos94.fuzzylogicgp.core.Utils;

import org.junit.Assert;
import org.junit.Test;

public class RandomTest {
    @Test
    public void aInstnace() {
        Utils.random.setSeed(1);
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Utils.randInt(0, 10);
        }
        System.out.println(sum);
        Assert.assertEquals(51, sum);
    }

    @Test
    public void concurrentAccess() throws InterruptedException {
        ExecutorService poolExecutor = Executors.newFixedThreadPool(10);

        Map<String, Integer> synchronizedMap = Collections.synchronizedMap(new HashMap<String, Integer>());
        List<Runnable> runnables = new ArrayList<>();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            int k = i;
            Runnable runnable = () -> {
                RNG rng = RNG.getRNG(1);
                int sum = 0;
                for (int j = 0; j < 10; j++) {
                    sum += rng.randInt(0, 10);
                }
                synchronizedMap.put("Runnable - " + k, sum);
            };
            runnables.add(runnable);
        }
        runnables.forEach(poolExecutor::execute);
        poolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        Integer expected = size*51;
        Assert.assertEquals(expected, synchronizedMap.values().stream().reduce(Integer::sum).get());
    }

}