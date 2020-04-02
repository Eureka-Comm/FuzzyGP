package com.castellanos.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Iterator;

import com.castellanos.fuzzylogicgp.base.Predicate;

public class TournamentSelection {
    private Predicate[] pop;
    private int num;
    private ArrayList<Predicate> selection;
    private Iterator<Predicate> next;

    public TournamentSelection(Predicate[] pop, int n) {
        this.pop = pop;
        this.num = n;
        this.selection = new ArrayList<>();
    }

    public void execute() {

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

}
