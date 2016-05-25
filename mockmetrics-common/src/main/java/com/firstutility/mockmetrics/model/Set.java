package com.firstutility.mockmetrics.model;


public class Set implements Metric {

    public static final String SET_TYPE = "|s";

    private String name;
    private int value;

    public static Set set() {
        return new Set();
    }

    public static Set parse(final String metric) {
        return set()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
    }

    public Set withName(final String name) {
        this.name = name;
        return this;
    }

    public Set withValue(final int value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public double getSampling() {
        return 0.0;
    }

    @Override
    public String toString() {
        return name + ":" + value + SET_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Set set = (Set) o;

        if (value != set.value) {
            return false;
        }
        return name != null ? name.equals(set.name) : set.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }
}
