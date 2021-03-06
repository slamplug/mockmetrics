package org.slamplug.mockmetrics.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slamplug.mockmetrics.utils.JsonFilterHelper;

import java.io.IOException;

@JsonFilter("timerJsonFilter")
public class Timer implements Metric {

    public static final String TIMER_TYPE = "|ms";
    public static final String SAMPLING_TYPE = "|@";

    private String name;
    private int value;
    private double sampling = 0.1;

    public static Timer timer() {
        return new Timer();
    }

    public static Timer parse(final String metric) {
        return timer()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))))
                .withSampling(Double.valueOf(metric.substring(metric.indexOf(SAMPLING_TYPE) + SAMPLING_TYPE.length())));
    }

    public static Timer parseJson(final String jsonMetric) throws IOException {
        return new ObjectMapper().readValue(jsonMetric, Timer.class);
    }

    public Timer withName(final String name) {
        this.name = name;
        return this;
    }

    public Timer withValue(final int value) {
        this.value = value;
        return this;
    }

    public Timer withSampling(final double sampling) {
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
        return sampling;
    }

    @Override
    public String toString() {
        return name + ":" + value + TIMER_TYPE + SAMPLING_TYPE + sampling;
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

        Timer timer = (Timer) o;

        /*if (value != timer.value) { // for timer don't match timed value.
            return false;
        }*/
        if (Double.compare(timer.sampling, sampling) != 0) {
            return false;
        }
        return name != null ? name.equals(timer.name) : timer.name == null;

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
