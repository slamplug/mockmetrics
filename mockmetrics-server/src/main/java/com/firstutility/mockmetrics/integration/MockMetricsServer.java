package com.firstutility.mockmetrics.integration;

import com.firstutility.mockmetrics.model.Metric;
import com.firstutility.mockmetrics.server.MockMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MockMetricsServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MockMetrics mockMetrics;

    public MockMetricsServer(Integer... ports) {
        mockMetrics = new MockMetrics(ports);
    }

    public static MockMetricsServer startMockMetricsServer(Integer... ports) {
        return new MockMetricsServer(ports);
    }

    public boolean isRunning() {
        return mockMetrics.isRunning();
    }

    public Integer getPort() {
        return mockMetrics.getPort();
    }

    public List<Integer> getPorts() {
        return this.getPorts();
    }

    public void stop() {
        mockMetrics.stop();
    }

    public void verify(final Metric metric) {
        mockMetrics.getMetricFilter().verify(metric);
    }

    public void verifyTimes(final Metric metric, final int times) {
        mockMetrics.getMetricFilter().verifyTimes(metric, times);
    }

}
