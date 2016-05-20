package com.firstutility.mockmetrics.example;

import com.firstutility.mockmetrics.integration.MockMetricsServer;
import com.firstutility.mockmetrics.utils.UDPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.firstutility.mockmetrics.integration.MockMetricsServer.startMockMetricsServer;
import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Gauge.gauge;

public class MockMetricsTest {

    private MockMetricsServer mockMetricsServer;
    private UDPClient udpClient;

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = startMockMetricsServer(9999);
    }

    @Test
    public void shouldAssertCounterMatricReceived() throws Exception {
        sendMetricWithPauseAfter("test.metric:1|c");
        mockMetricsServer.verify(counter().withName("test.metric").withValue(1));
    }

    @Test
    public void shouldAssertGaugeMatricReceived() throws Exception {
        sendMetricWithPauseAfter("test.metric:1|g");
        mockMetricsServer.verify(gauge().withName("test.metric").withValue(1));
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }

    private void sendMetricWithPauseAfter(final String metric) throws Exception {
        new UDPClient(9999).send(metric);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
        }
    }

}
