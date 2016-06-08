package org.slamplug.mockmetrics.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slamplug.mockmetrics.client.MockMetricsClient;
import org.slamplug.mockmetrics.integration.MockMetricsServer;
import org.slamplug.mockmetrics.utils.UDPClient;

import static org.slamplug.mockmetrics.model.Gauge.gauge;
import static org.slamplug.mockmetrics.verify.Verification.verification;
import static org.slamplug.mockmetrics.verify.Verifications.verifications;

public class MockMetricsUsingClientTest {

    private MockMetricsServer mockMetricsServer;
    private MockMetricsClient mockMetricsClient;

    private UDPClient udpClient = new UDPClient("localhost", 9999);

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = MockMetricsServer.startMockMetricsServer(9999);
        mockMetricsClient = MockMetricsClient.startMockMetricsClient("localhost", 9999);
    }

    @Test
    public void shouldAssertGaugeMetricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|g");

        mockMetricsClient.verify(verifications().withVerifications(
                verification().withMetric(
                        gauge().withName("test.metric").withValue(1)
                ))
        );
    }

    @Test
    public void shouldAssertMultipleGaugeMetricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric.1:1|g");
        udpClient.sendMetricWithPauseAfter("test.metric.2:2|g");

        mockMetricsClient.verify(verifications().withVerifications(
                verification().withMetric(
                        gauge().withName("test.metric.1").withValue(1)
                ),
                verification().withMetric(
                        gauge().withName("test.metric.2").withValue(2)
                ))
        );
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertGaugeMetricNotReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");

        mockMetricsClient.verify(verifications().withVerifications(
                verification().withMetric(
                        gauge().withName("test.metric").withValue(1)
                ))
        );
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertMultipleGaugeMetricNotReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric.1:1|c");
        udpClient.sendMetricWithPauseAfter("test.metric.2:2|g");

        mockMetricsClient.verify(verifications().withVerifications(
                verification().withMetric(
                        gauge().withName("test.metric.1").withValue(1)
                ),
                verification().withMetric(
                        gauge().withName("test.metric.2").withValue(2)
                ))
        );
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }
}
