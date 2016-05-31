package org.slamplug.mockmetrics.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slamplug.mockmetrics.verify.Verifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockMetricsClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        // throw assertion if required
        assert response.getLeft() : response.getRight();
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
            HttpPost httpPost = new HttpPost(baseUrl + "/verify");

            httpPost.addHeader("Content-Type", "application/text");
            httpPost.addHeader("Accept", "application/text");
            httpPost.setEntity(new StringEntity(body));

            responseBody = trimResponseBody(httpclient.execute(httpPost, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws IOException {
                    if (httpResponse.getEntity() != null) {
                        return httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
                    } else {
                        throw new IOException("ContextIO returned empty response!");
                    }
                }
            }));
            logger.debug("----------------------------------------");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");

        } catch (IOException e) {
            httpclient.close();
        }

        return Pair.of(responseBody != null && responseBody.equals("OK"), responseBody);
    }

    private String trimResponseBody(final String in) {
        return (!StringUtils.isEmpty(in)) ? in.replace("\r\n", "") : in;
    }
}
