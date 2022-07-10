package com.castellanos94.fuzzylogicgp.core;

import java.util.List;

public class DiscoveryResult extends ResultTask {
    protected final List<Record> values;

    public DiscoveryResult(List<Record> values) {
        this.values = values;
    }

    public static class Record {
        protected final Double fitness;
        protected final String expression;
        protected final NodeTree data;

        public Record(Double fitness, NodeTree data) {
            this.fitness = fitness;
            this.expression = data.toString();
            this.data = data;
        }

        public NodeTree getData() {
            return data;
        }

        public String getExpression() {
            return expression;
        }

        public Double getFitness() {
            return fitness;
        }

        @Override
        public String toString() {
            return String.format("%.5f, %s %s", fitness, expression, data);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (values != null) {
            for (Record record : values) {
                buffer.append(record).append("\n");
            }
        }
        return buffer.toString();
    }
}