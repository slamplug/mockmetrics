package org.slamplug.mockmetrics.client;

import org.slamplug.mockmetrics.model.Metric;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MockMetricsClient {

    private final String baseUrl;

    public MockMetricsClient(final String host, final int port) {
        baseUrl = "http://" + host + ":" + port;
    }

    public static MockMetricsClient startMockMetricsClient(final String host, final int port) {
        return new MockMetricsClient(host, port);
    }

    /**
     * Verify
     *
     * @param metric
     * @throws Exception
     */
    public void verify(final Metric metric) throws Exception {
        // metric to string
        String metricString = metric.toString();
        // http request to server
        Metric returnedMetric = verifyByHttp(metricString);
        // get response
        assert returnedMetric.equals(metric);
    }

    /**
     * Does http put
     *
     * @return
     * @throws Exception
     */
    private Metric verifyByHttp(final String metric) throws Exception {
        // http request to server
        CloseableHttpClient httpclient = HttpClients.createDefault();
        Metric returnMetric = null;
        try {
            HttpPut httpPut = new HttpPut(baseUrl + "/verify");

            httpPut.addHeader("Content-Type", "application/text");
            httpPut.addHeader("Accept", "application/text");
            httpPut.setEntity(new StringEntity(metric));

            String responseBody = httpclient.execute(httpPut, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws IOException {
                    if (httpResponse.getEntity() != null) {
                        return httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
                    } else {
                        throw new IOException("ContextIO returned empty response!");
                    }
                }
            });
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        } catch (IOException e) {
            httpclient.close();
        }

        return returnMetric;
    }
}
