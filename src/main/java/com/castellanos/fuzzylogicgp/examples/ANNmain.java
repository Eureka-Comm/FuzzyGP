package com.castellanos.fuzzylogicgp.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.DataSet;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;


/**
 * @author s210
 */
public class ANNmain {

    public static int generatRandomPositiveNegitiveValue(int max) {
        if(max%2 == 1)
            max ++;
        
        int min = (int) max/2;
        Random Rand = new Random();
        return -min + (int)Rand.nextInt(max);
    }
    public static void main(String[] args) {
        DataSet train = new DataSet("tinto.csv");
        List<List<Double>> X = train.getDataWithOutClass();

        List<List<Sigmoid>> MFw = new ArrayList<>();
        for (int w = 0; w < X.size(); w++ ) {
            List<Sigmoid> MFh = new ArrayList<Sigmoid>();
            for (int h =0; h<3; h++){
                Sigmoid v = new Sigmoid(0.5,generatRandomPositiveNegitiveValue(10));
                MFh.add(v);
            }
            MFw.add(MFh);
        }
        
    }
}