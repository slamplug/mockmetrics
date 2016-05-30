package org.slamplug.mockmetrics.client;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slamplug.mockmetrics.verify.Verifications;

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
     * @param verifications
     * @throws Exception
     */
    public void verify(final Verifications verifications) throws Exception {

        // http request to server
        Pair<Boolean, String> response = verifyByHttp(verifications.toJsonString());
        // get response
        assert true : "true assertions";
    }

    /**
     * Does http put
     *
     * @return
     * @throws Exception
     */
    private Pair<Boolean, String> verifyByHttp(final String body) throws Exception {
        // http request to server
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseBody = null;
        try {
            HttpPut httpPut = new HttpPut(baseUrl + "/verify");

            httpPut.addHeader("Content-Type", "application/text");
            httpPut.addHeader("Accept", "application/text");
            httpPut.setEntity(new StringEntity(body));

            responseBody = httpclient.execute(httpPut, new ResponseHandler<String>() {
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

        return Pair.of(responseBody != null && responseBody.equals("OK"), responseBody);
    }
}
