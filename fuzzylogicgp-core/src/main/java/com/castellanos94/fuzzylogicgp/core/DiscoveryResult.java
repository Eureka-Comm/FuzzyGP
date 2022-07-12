package com.castellanos94.fuzzylogicgp.core;

import java.util.List;

public class DiscoveryResult extends ResultTask {
    protected final List<Record> values;

    public DiscoveryResult(List<Record> values) {
        this.values = values;
    }

    public List<Record> getValues() {
        return values;
    }

    public static class Record {
        protected Double fitness;
        protected String expression;
        protected String data;

        public Record() {
            this.fitness = null;
            this.expression = null;
            this.data = null;
        }

        static Record of(Double fitness, String expression, String data) {
            return new Record(fitness, expression, data);
        }

        public Record(Double fitness, String expression, String data) {
            this.fitness = fitness;
            this.expression = expression;
            this.data = data;
        }
        
        public String getData() {
            return data;
        }

        public String getExpression() {
            return expression;
        }

        public Double getFitness() {
            return fitness;
        }

        public void setData(String data) {
            this.data = data;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public void setFitness(Double fitness) {
            this.fitness = fitness;
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