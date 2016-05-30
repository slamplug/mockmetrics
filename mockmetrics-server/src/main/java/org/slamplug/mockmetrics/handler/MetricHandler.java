package org.slamplug.mockmetrics.handler;

import org.slamplug.mockmetrics.model.*;

import java.util.ArrayList;
import java.util.List;

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
                metricList.add(Counter.parse(metric));
            } else if (metric.contains(Gauge.GAUGE_TYPE)) {
                metricList.add(Gauge.parse(metric));
            } else if (metric.contains(Timer.TIMER_TYPE)) {
                metricList.add(Timer.parse(metric));
            } else if (metric.contains(Set.SET_TYPE)) {
                metricList.add(Set.parse(metric));
            }
        }
        return metricList;
    }
}
