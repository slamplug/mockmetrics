package com.firstutility.mockmetrics.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;

@JsonFilter("counterJsonFilter")
public class Counter implements Metric {

    public static final String METRIC_TYPE = "counter";
    public static final String COUNTER_TYPE = "|c";
    public static final String SAMPLING_TYPE = "|@";

    private String type = METRIC_TYPE;
    private String name;
    private int value;
    private double sampling;
    private boolean hasSampling = false;

    public static Counter counter() {
        return new Counter();
    }

    public static Counter parse(final String metric) {
        Counter counter = counter()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
        if (metric.contains(SAMPLING_TYPE)) {
            counter.withSampling(Double.valueOf(metric.substring(metric.indexOf(SAMPLING_TYPE) + SAMPLING_TYPE.length())));
        }
        return counter;
    }

    public Counter withName(final String name) {
        this.name = name;
        return this;
    }

    public Counter withValue(final int value) {
        this.value = value;
        return this;
    }

    public Counter withSampling(final double sampling) {
        this.sampling = sampling;
        this.hasSampling = true;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public double getSampling() {
        return sampling;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return name + ":" + value + COUNTER_TYPE + ((this.hasSampling) ? (SAMPLING_TYPE + sampling) : "");
    }

    @Override
    public String toJsonString() throws JsonProcessingException {
        List<String> filterlist = (!hasSampling) ?
                Arrays.asList("type", "name", "value") :
                Arrays.asList("type", "name", "value", "sampling");
        return new ObjectMapper().setFilterProvider(new SimpleFilterProvider().addFilter("counterJsonFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept(Sets.newHashSet(filterlist))))
                .writeValueAsString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Counter counter = (Counter) o;

        if (value != counter.value) {
            return false;
        }
        if (Double.compare(counter.sampling, sampling) != 0) {
            return false;
        }
        return name != null ? name.equals(counter.name) : counter.name == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        temp = Double.doubleToLongBits(sampling);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
