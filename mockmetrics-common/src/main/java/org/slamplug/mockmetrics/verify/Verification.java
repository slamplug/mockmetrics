package org.slamplug.mockmetrics.verify;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slamplug.mockmetrics.model.Metric;
import org.slamplug.mockmetrics.utils.JsonFilterHelper;

import java.io.IOException;

@JsonFilter("verificationJsonFilter")
public class Verification {

    private Metric metric;
    private int times = 1;

    public Verification() {
    }

    public static Verification verification() {
        return new Verification();
    }

    public Verification withMetric(final Metric metric) {
        this.metric = metric;
        return this;
    }

    public Verification withTimes(final int times) {
        this.times = times;
        return this;
    }

    public Metric getMetric() {
        return metric;
    }

    public int getTimes() {
        return times;
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().setFilterProvider(JsonFilterHelper.addFilters(this))
                .writeValueAsString(this);
    }

    public static Verification parseJson(final String jsonMetric) throws IOException {
        return new ObjectMapper().readValue(jsonMetric, Verification.class);
    }

    @Override
    public String toString() {
        return "Verification{" +
                "metric=" + metric +
                ", times=" + times +
                '}';
    }
}
