package com.firstutility.mockmetrics.client;

import com.firstutility.mockmetrics.model.Gauge;
import com.firstutility.mockmetrics.model.Metric;

public class MockMetricsClient {

    public static MockMetricsClient startMockMetricsClient(final String host, final int port) {
        // create Apache http client
        return new MockMetricsClient();
    }

    public void verify(final Metric metric) {
        // metric to string
        // http request to server
        // get response
        // assert response OK
    }

    private Metric verifyByHttp() {
        return new Gauge();
    }
}
