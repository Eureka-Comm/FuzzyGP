package com.castellanos.fuzzylogicgp.core;

import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.logic.ALogic;

/**
 * fuzzy compensatory logical knowledge discovery
 * @author Castellanos Alvarez, Alejandro.
 * @since Oct, 19.
 * @version 0.0.1
 */
public class KDFLC {
    private Predicate patternPredicate;
    
    private ALogic logic;

    private void genetic(){}
    private void mutation(Predicate[] population){}

    private Predicate[] crossover(Predicate[] population){
        Predicate[] childs  = new Predicate[(population.length%2 == 0)?population.length/2:population.length/2 +1];

        return childs;
    }
    private void evaluationChromosome(Predicate[] population){}
}