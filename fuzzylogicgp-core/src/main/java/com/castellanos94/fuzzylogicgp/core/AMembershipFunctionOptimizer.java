package com.castellanos94.fuzzylogicgp.core;

import java.util.List;
import java.util.Set;

import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;

/**
 * Abstract class for membership function optimizer
 */
public abstract class AMembershipFunctionOptimizer {
    protected Logic logic;

    public AMembershipFunctionOptimizer(Logic logic) {
        this.logic = logic;
    }

    public abstract NodeTree execute(NodeTree predicate);

    protected abstract List<MembershipFunction> crossover(MembershipFunction... functions);

    protected abstract MembershipFunction mutation(MembershipFunction membershipFunction);

    protected abstract MembershipFunction repair(MembershipFunction membershipFunction);

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }
}
