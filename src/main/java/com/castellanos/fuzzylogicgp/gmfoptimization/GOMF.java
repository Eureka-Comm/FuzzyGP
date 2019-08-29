/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.gmfoptimization;

import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import java.util.ArrayList;
import java.util.List;
import tech.tablesaw.api.Table;

/**
 * Generalized Optimizer of Membership Functions
 *
 * @author hp
 */
public class GOMF {

    private ALogic logic;
    private float mut_percentage;
    private int adj_num_pop;
    private int adj_iter;
    private int adj_truth_value;
    private Table data;
    
    private Predicate predicatePattern;

    public GOMF(ALogic logic, float mut_percentage, int adj_num_pop, int adj_iter, int adj_truth_value) {
        this.logic = logic;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_iter = adj_iter;
        this.adj_truth_value = adj_truth_value;
    }

    public void optimize(Predicate p) {
        this.predicatePattern = p;
    }
    public List<Predicate> makePop(){
        List<Predicate> pop = new ArrayList<>();
        
        return pop;
    }
    public void mutation(Predicate p){
        
    }
    public Predicate crossover( Predicate parent1, Predicate parent2){
        Predicate p = new Predicate();
        
        return p;
    }

    public void optimize(Table data, Predicate p) {
        this.data = data;
        this.predicatePattern = p;
    }

    public void setData(Table data) {
        this.data = data;
    }

    public void setPredicate(Predicate predicatePattern) {
        this.predicatePattern = predicatePattern;
    }

    public Table getData() {
        return data;
    }

    public Predicate getPredicate() {
        return predicatePattern;
    }

    public ALogic getLogic() {
        return logic;
    }

    public void setLogic(ALogic logic) {
        this.logic = logic;
    }

    public float getMut_percentage() {
        return mut_percentage;
    }

    public void setMut_percentage(float mut_percentage) {
        this.mut_percentage = mut_percentage;
    }

    public int getAdj_num_pop() {
        return adj_num_pop;
    }

    public void setAdj_num_pop(int adj_num_pop) {
        this.adj_num_pop = adj_num_pop;
    }

    public int getAdj_iter() {
        return adj_iter;
    }

    public void setAdj_iter(int adj_iter) {
        this.adj_iter = adj_iter;
    }

    public int getAdj_truth_value() {
        return adj_truth_value;
    }

    public void setAdj_truth_value(int adj_truth_value) {
        this.adj_truth_value = adj_truth_value;
    }

}
