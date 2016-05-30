package org.slamplug.mockmetrics.example;

import org.slamplug.mockmetrics.client.MockMetricsClient;
import org.slamplug.mockmetrics.integration.MockMetricsServer;
import org.slamplug.mockmetrics.utils.UDPClient;
import org.junit.Before;
import org.junit.Test;
import org.slamplug.mockmetrics.model.Gauge;

public class MockMetricsIntegrationTest {

    private MockMetricsServer mockMetricsServer;
    private MockMetricsClient mockMetricsClient;

    private UDPClient udpClient = new UDPClient("localhost", 9999);

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = MockMetricsServer.startMockMetricsServer(9999);
        mockMetricsClient = MockMetricsClient.startMockMetricsClient("localhost", 9999);
    }

    @Test
    public void shouldAssertGaugeMatricReceived() throws Exception {
        udpClient.sendMetricWithPauseAfter("test.metric:1|c");

        mockMetricsClient.verify(Gauge.gauge().withName("test.metric").withValue(1));
    }
}
