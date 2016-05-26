package com.firstutility.mockmetrics.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@JsonFilter("gaugeJsonFilter")
public class Gauge implements Metric {

    public static final String METRIC_TYPE = "gauge";
    public static final String GAUGE_TYPE = "|g";

    private String type = METRIC_TYPE;
    private String name;
    private int value;
    private boolean increment = false;

    public static Gauge gauge() {
        return new Gauge();
    }

    public static Gauge parse(final String metric) {
        Gauge gauge = gauge()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
        gauge.increment = (metric.contains("+") || metric.contains("-"));
        return gauge;
    }

    public Gauge withName(final String name) {
        this.name = name;
        return this;
    }

    public Gauge withValue(final int value) {
        this.value = value;
        return this;
    }

    public Gauge withIncrement() {
        this.increment = true;
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

    public boolean isIncrement() {
        return this.increment;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return name + ":" + ((isIncrement() && (value >= 0)) ? "+" : "") + value + GAUGE_TYPE;
    }

    @Override
    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().setFilterProvider(new SimpleFilterProvider().addFilter("gaugeJsonFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("type", "name", "value", "increment")))
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
