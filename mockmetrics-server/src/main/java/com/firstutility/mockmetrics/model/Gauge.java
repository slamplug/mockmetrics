package com.firstutility.mockmetrics.model;


public class Gauge implements Metric {

    public static final String GAUGE_TYPE = "|g";

    private String name;
    private int value;

    public static Gauge gauge() {
        return new Gauge();
    }

    public Gauge withName(final String name) {
        this.name = name;
        return this;
    }

    public Gauge withValue(final int value) {
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
        return name + ":" + value + GAUGE_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Gauge gauge = (Gauge) o;

        if (value != gauge.value) {
            return false;
        }
        return name != null ? name.equals(gauge.name) : gauge.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }
}