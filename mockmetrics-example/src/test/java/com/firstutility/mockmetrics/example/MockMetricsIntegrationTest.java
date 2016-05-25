package com.firstutility.mockmetrics.example;

import com.firstutility.mockmetrics.client.MockMetricsClient;
import com.firstutility.mockmetrics.integration.MockMetricsServer;
import com.firstutility.mockmetrics.utils.UDPClient;
import org.junit.Before;
import org.junit.Test;

import static com.firstutility.mockmetrics.client.MockMetricsClient.startMockMetricsClient;
import static com.firstutility.mockmetrics.integration.MockMetricsServer.startMockMetricsServer;
import static com.firstutility.mockmetrics.model.Gauge.gauge;

public class MockMetricsIntegrationTest {

    private MockMetricsServer mockMetricsServer;
    private MockMetricsClient mockMetricsClient;

    private UDPClient udpClient = new UDPClient("localhost", 9999);

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = startMockMetricsServer(9999);
        mockMetricsClient = startMockMetricsClient("localhost", 9999);
    }

    @Test
    public void shouldAssertGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");

        mockMetricsClient.verify(gauge().withName("test.metric").withValue(1));
    }
}
