package com.castellanos94.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class TournamentSelection {
    private NodeTree[] pop;
    private int num;
    private ArrayList<NodeTree> selection;
    private Iterator<NodeTree> next;
    private static final Random rand = new Random();

    public TournamentSelection(NodeTree[] pop, int n) {
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
            if (pop[a].getFitness().compareTo(pop[b].getFitness()) > 0 || rand.nextDouble() > 0.5) {
                selection.add(pop[a]);
            } else {
                selection.add(pop[b]);
            }
        }
        next = selection.iterator();
    }

    /**
     * @return the next
     */
    public NodeTree getNext() {
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

    public ArrayList<NodeTree> getAll() {
        return selection;
    }

}
