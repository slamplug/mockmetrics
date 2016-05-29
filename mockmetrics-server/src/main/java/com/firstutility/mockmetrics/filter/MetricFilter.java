package com.firstutility.mockmetrics.filter;

import com.firstutility.mockmetrics.model.Metric;
import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MetricFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EvictingQueue<Metric> metricQueue = EvictingQueue.create(100);

    public /* synchronized */ List<Metric> onMetrics(List<Metric> metrics) {
        metricQueue.addAll(metrics);
        logger.debug("metrics queue :" + this.metricQueue);
        return metrics;
    }

    public /* synchronized */ Metric onMetric(Metric metric) {
        metricQueue.add(metric);
        logger.debug("metrics queue :" + this.metricQueue);
        return metric;
    }

    public /* synchronized */ void reset() {
        metricQueue.clear();
    }

    public void verify(Metric metric) {
        verifyTimes(metric, 1);
    }

    public void verifyTimes(final Metric metric, final int times) {
        logger.debug("verifyTimes(), queue  :" + this.metricQueue);
        logger.debug("verifyTimes(), metric :" + this.metricQueue);

        LinkedList<Metric> metricsList = new LinkedList<Metric>(this.metricQueue);
        logger.debug("VERIFY metricsList :" + metricsList);

        if (metric != null) {
            List<Metric> matchingMetrics = new ArrayList<Metric>();
            for (Metric aMetric : metricsList) {
                if (metric.equals(aMetric)) {
                    matchingMetrics.add(aMetric);
                }
            }

            boolean verified = true;

            if (times != 0 && matchingMetrics.isEmpty()) {
                verified = false;
            } else if (times != matchingMetrics.size()) {
                verified = false;
            } else if (matchingMetrics.size() < times) {
                verified = false;
            }

            assert verified : "Metric not found " + times + ", expected:<" + metric.toString() + "> but was:<" + metricsList + ">";
        }
    }
}
