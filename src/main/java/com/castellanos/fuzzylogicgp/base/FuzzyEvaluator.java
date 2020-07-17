package com.castellanos.fuzzylogicgp.base;

import java.io.Serializable;

import com.castellanos.fuzzylogicgp.logic.Logic;
import com.castellanos.fuzzylogicgp.membershipfunction.GCLV_MF;

public class FuzzyEvaluator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1614572590408584283L;

    protected Logic logic;

    public FuzzyEvaluator(Logic logic) {
        this.logic = logic;
    }

    public Double evaluate(StateNode stateNode, Number value) {
        loadData(stateNode);
        return stateNode.getMembershipFunction().evaluate(value);
    }

    public Double evaluate(StateNode stateNode, String key) {
        loadData(stateNode);
        return stateNode.getMembershipFunction().evaluate(key);
    }

    private void loadData(StateNode stateNode) {
        if (stateNode.getMembershipFunction() instanceof GCLV_MF) {
            // TODO: SOMETHING
        }
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

}