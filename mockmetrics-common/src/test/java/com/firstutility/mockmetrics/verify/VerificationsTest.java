package com.firstutility.mockmetrics.verify;

import com.firstutility.mockmetrics.model.Counter;
import com.firstutility.mockmetrics.model.Set;
import org.junit.Test;

import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Set.set;
import static com.firstutility.mockmetrics.verify.Verification.verification;
import static com.firstutility.mockmetrics.verify.Verifications.verifications;
import static org.junit.Assert.assertEquals;

public class VerificationsTest {

    @Test
    public void testToJsonStringVerificationsWithSingleVerification() throws Exception {
        Counter counter = counter().withName("test.metric.1").withValue(98);
        Verification verification = verification().withMetric(counter);
        Verifications verifications = verifications().withVerifications(verification);

        assertEquals("{\"verifications\":[{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric.1\",\"value\":98},\"times\":1}]}",
                verifications.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationsWithCounterWithMultipleVerifications() throws Exception {
        Counter counter = counter().withName("test.metric.1").withValue(97);
        Set set = set().withName("test.metric.2").withValue(96);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter),
                verification().withMetric(set)
        );

        assertEquals("{\"verifications\":[" +
                        "{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric.1\",\"value\":97},\"times\":1}," +
                        "{\"metric\":{\"type\":\"set\",\"name\":\"test.metric.2\",\"value\":96},\"times\":1}" +
                        "]}",
                verifications.toJsonString());
    }
}
