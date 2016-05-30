package org.slamplug.mockmetrics.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slamplug.mockmetrics.utils.JsonFilterHelper;

import java.io.IOException;

@JsonFilter("counterJsonFilter")
public class Counter implements Metric {

    public static final String COUNTER_TYPE = "|c";
    public static final String SAMPLING_TYPE = "|@";

    private String name;
    private int value;
    private Double sampling = null;

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

    public static Counter parseJson(final String jsonMetric) throws IOException {
        Counter counter = new ObjectMapper().readValue(jsonMetric, Counter.class);
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

    public Counter withSampling(final Double sampling) {
        this.sampling = sampling;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public Double getSampling() {
        return this.sampling;
    }

    public boolean hasSampling() {
        return (this.sampling != null);
    }

    /**
     * returns gorets:1|c or gorets:1|c@0.1
     *
     * @return
     */
    @Override
    public String toString() {
        return name + ":" + value + COUNTER_TYPE + ((this.sampling != null) ? (SAMPLING_TYPE + sampling) : "");
    }

    /**
     * Returns { "name": "gorets", "value": 1 } or { "name": "gorets", "value": 1, "sampling": 0.1 }
     *
     * @return
     * @throws JsonProcessingException
     */
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

        Counter counter = (Counter) o;

        if (value != counter.value) {
            return false;
        }
        if (!name.equals(counter.name)) {
            return false;
        }
        return (sampling != null) ? sampling.equals(counter.sampling) : counter.sampling == null;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value;
        result = 31 * result + (sampling != null ? sampling.hashCode() : 0);
        return result;
    }
}
