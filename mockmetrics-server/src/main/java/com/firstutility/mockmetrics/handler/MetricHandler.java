package com.firstutility.mockmetrics.handler;

import com.firstutility.mockmetrics.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Gauge.gauge;
import static com.firstutility.mockmetrics.model.Set.set;
import static com.firstutility.mockmetrics.model.Timer.timer;

public class MetricHandler {

    /**
     * Handle metrics
     * Counters: gorets:1|c or gorets:1|c|@0.1
     * Gauges: gaugor:333|g or gaugor:+10|g or gaugor:-4|g
     * Timing: glork:320|ms|@0.1
     * Sets: uniques:765|s
     * Can have multiple metrics seperated by new lines
     *
     * @param metrics
     */
    public static List<Metric> handleMetrics(String... metrics) {
        List<Metric> metricList = new ArrayList<>();
        for (String metric : metrics) {
            if (metric.contains(Counter.COUNTER_TYPE)) {
                metricList.add(parseCounter(metric));
            } else if (metric.contains(Gauge.GAUGE_TYPE)) {
                metricList.add(parseGauge(metric));
            } else if (metric.contains(Timer.TIMER_TYPE)) {
                metricList.add(parseTimer(metric));
            } else if (metric.contains(Set.SET_TYPE)) {
                metricList.add(parseSet(metric));
            }
        }
        return metricList;
    }

    /**
     * Parse counter from string
     *
     * @param metric
     * @return
     */
    public static Counter parseCounter(final String metric) {
        Counter counter = counter()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
        if (metric.contains(Counter.SAMPLING_TYPE)) {
            counter.withSampling(Double.valueOf(metric.substring(metric.indexOf(Counter.SAMPLING_TYPE) + Counter.SAMPLING_TYPE.length())));
        }
        return counter;
    }

    /**
     * Parse guage from string
     *
     * @param metric
     * @return
     */
    public static Gauge parseGauge(final String metric) {
        return gauge()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
    }

    /**
     * Parse timer metric
     *
     * @param metric
     * @return
     */
    public static Timer parseTimer(final String metric) {
        return timer()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))))
                .withSampling(Double.valueOf(metric.substring(metric.indexOf(Timer.SAMPLING_TYPE) + Timer.SAMPLING_TYPE.length())));
    }

    /**
     * Parse set metric
     *
     * @param metric
     * @return
     */
    public static Set parseSet(final String metric) {
        return set()
                .withName(metric.substring(0, metric.indexOf(":")))
                .withValue(Integer.valueOf(metric.substring(metric.indexOf(":") + 1, metric.indexOf("|"))));
    }
}
