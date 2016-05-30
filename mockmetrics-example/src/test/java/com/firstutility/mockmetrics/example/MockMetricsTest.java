package com.firstutility.mockmetrics.example;

import com.firstutility.mockmetrics.integration.MockMetricsServer;
import com.firstutility.mockmetrics.utils.UDPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.firstutility.mockmetrics.integration.MockMetricsServer.startMockMetricsServer;
import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Gauge.gauge;
import static com.firstutility.mockmetrics.verify.Verification.verification;

public class MockMetricsTest {

    private MockMetricsServer mockMetricsServer;
    private UDPClient udpClient = new UDPClient("localhost", 9999);

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = startMockMetricsServer(9999);
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
        mockMetricsServer.verify(verification().withMetric(counter().withName("test.metric").withValue(1)));
    }

    @Test
    public void shouldAssertGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|g");
        mockMetricsServer.verify(gauge().withName("test.metric").withValue(1));
    }

    @Test
    public void shouldAssertVerificationWithGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|g");
        mockMetricsServer.verify(verification().withMetric(gauge().withName("test.metric").withValue(1)));
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }
}
