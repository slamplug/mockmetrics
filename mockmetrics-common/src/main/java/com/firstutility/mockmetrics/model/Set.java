package com.firstutility.mockmetrics.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstutility.mockmetrics.utils.JsonFilterHelper;

import java.io.IOException;

@JsonFilter("setJsonFilter")
public class Set implements Metric {

    public static final String METRIC_TYPE = "set";
    public static final String SET_TYPE = "|s";

    private String type = METRIC_TYPE;
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

    public static Set parseJson(final String jsonMetric) throws IOException {
        return new ObjectMapper().readValue(jsonMetric, Set.class);
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

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return name + ":" + value + SET_TYPE;
    }

    @Override
    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().setFilterProvider(JsonFilterHelper.addFilters(this))
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
