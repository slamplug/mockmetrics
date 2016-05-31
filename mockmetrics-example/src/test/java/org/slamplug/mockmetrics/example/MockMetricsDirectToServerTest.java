package org.slamplug.mockmetrics.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slamplug.mockmetrics.integration.MockMetricsServer;
import org.slamplug.mockmetrics.model.Gauge;
import org.slamplug.mockmetrics.utils.UDPClient;
import org.slamplug.mockmetrics.verify.Verification;

import static org.slamplug.mockmetrics.model.Counter.counter;

public class MockMetricsDirectToServerTest {

    private MockMetricsServer mockMetricsServer;
    private UDPClient udpClient = new UDPClient("localhost", 9999);

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = MockMetricsServer.startMockMetricsServer(9999);
    }

    @Test
    public void shouldAssertCounterMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");
        mockMetricsServer.verify(counter().withName("test.metric").withValue(1));
    }

    @Test
    public void shouldAssertCounterMatricReceivedMultipleTimes() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");
        mockMetricsServer.verifyTimes(counter().withName("test.metric").withValue(1), 2);
    }

    @Test
    public void shouldAssertVerificationWithCounterMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");
        mockMetricsServer.verify(Verification.verification().withMetric(counter().withName("test.metric").withValue(1)));
    }

    @Test
    public void shouldAssertGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|g");
        mockMetricsServer.verify(Gauge.gauge().withName("test.metric").withValue(1));
    }

    @Test
    public void shouldAssertVerificationWithGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|g");
        mockMetricsServer.verify(Verification.verification().withMetric(Gauge.gauge().withName("test.metric").withValue(1)));
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }
}
