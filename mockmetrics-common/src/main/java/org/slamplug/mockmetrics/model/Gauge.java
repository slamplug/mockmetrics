package org.slamplug.mockmetrics.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slamplug.mockmetrics.utils.JsonFilterHelper;

import java.io.IOException;

@JsonFilter("gaugeJsonFilter")
public class Gauge implements Metric {

    public static final String GAUGE_TYPE = "|g";

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

    public static Gauge parseJson(final String jsonMetric) throws IOException {
        return new ObjectMapper().readValue(jsonMetric, Gauge.class);
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
    public Double getSampling() {
        return 0.0;
    }

    public boolean isIncrement() {
        return this.increment;
    }

    @Override
    public String toString() {
        return name + ":" + ((isIncrement() && (value >= 0)) ? "+" : "") + value + GAUGE_TYPE;
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
