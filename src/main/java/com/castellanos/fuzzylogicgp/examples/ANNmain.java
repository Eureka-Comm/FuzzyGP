package com.castellanos.fuzzylogicgp.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.DataSet;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid_MF;


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
        List<Double> Y = train.getDataClass();

        List<List<Sigmoid_MF>> MFw = new ArrayList<>();
        for (int w = 0; w < X.size(); w++ ) {
            List<Sigmoid_MF> MFh = new ArrayList<Sigmoid_MF>();
            for (int h =0; h<3; h++){
                Sigmoid_MF v = new Sigmoid_MF(0.5,generatRandomPositiveNegitiveValue(10));
                MFh.add(v);
            }
            MFw.add(MFh);
        }
        
    }
}