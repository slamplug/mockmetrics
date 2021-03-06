package org.slamplug.mockmetrics.integration;

import org.slamplug.mockmetrics.model.Metric;
import org.slamplug.mockmetrics.server.MockMetrics;
import org.slamplug.mockmetrics.verify.Verification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MockMetricsServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MockMetrics mockMetrics;

    public MockMetricsServer(final Integer... serverPortBindings) {
        mockMetrics = new MockMetrics(serverPortBindings);
    }

    public static MockMetricsServer startMockMetricsServer(final Integer... serverPortBindings) {
        return new MockMetricsServer(serverPortBindings);
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
        verifyTimes(metric, 1);
    }

    public void verify(final Verification verification) {
        verifyTimes(verification, 1);
    }

    public void verifyTimes(final Metric metric, final int times) {
        mockMetrics.getMetricFilter().verifyTimes(metric, times);
    }

    public void verifyTimes(final Verification verification, final int times) {
        mockMetrics.getMetricFilter().verifyTimes(verification.getMetric(), times);
    }
}
