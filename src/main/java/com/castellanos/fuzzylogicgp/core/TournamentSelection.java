package com.castellanos.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.Predicate;

public class TournamentSelection {
    private Predicate[] pop;
    private int num;
    private ArrayList<Predicate> selection;
    private Iterator<Predicate> next;
    private static final Random rand = new Random();

    public TournamentSelection(Predicate[] pop, int n) {
        this.pop = pop;
        this.num = n;
        this.selection = new ArrayList<>();
    }

    public void execute() {
        for (int i = 0; i < num; i++) {
            int a = rand.nextInt(pop.length);
            int b = rand.nextInt(pop.length);
            int intents = 0;
            while (a == b && intents < pop.length) {
                b = rand.nextInt(pop.length);
                intents++;
            }
            if (pop[a].getFitness().compareTo(pop[b].getFitness()) > 0) {
                selection.add(pop[a]);
            } else {
                selection.add(pop[b]);
            }

        }
    }

    /**
     * @return the next
     */
    public Predicate getNext() {
        if (next != null) {
            if (next.hasNext()) {
                return next.next();
            } else {
                next = selection.iterator();
                return next.next();
            }
        }
        return null;
    }

    public ArrayList<Predicate> getAll() {
        return selection;
    }

}
