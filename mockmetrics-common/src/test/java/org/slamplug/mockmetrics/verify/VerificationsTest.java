package org.slamplug.mockmetrics.verify;

import org.slamplug.mockmetrics.model.Counter;
import org.slamplug.mockmetrics.model.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VerificationsTest {

    @Test
    public void testToJsonStringVerificationsWithSingleVerification() throws Exception {
        Counter counter = Counter.counter().withName("test.metric.1").withValue(98);
        Verification verification = Verification.verification().withMetric(counter);
        Verifications verifications = Verifications.verifications().withVerifications(verification);

        assertEquals("{\"verifications\":[{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric.1\",\"value\":98},\"times\":1}]}",
                verifications.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationsWithCounterWithMultipleVerifications() throws Exception {
        Counter counter = Counter.counter().withName("test.metric.1").withValue(97);
        Set set = Set.set().withName("test.metric.2").withValue(96);
        Verifications verifications = Verifications.verifications().withVerifications(
                Verification.verification().withMetric(counter),
                Verification.verification().withMetric(set)
        );

        assertEquals("{\"verifications\":[" +
                        "{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric.1\",\"value\":97},\"times\":1}," +
                        "{\"metric\":{\"type\":\"set\",\"name\":\"test.metric.2\",\"value\":96},\"times\":1}" +
                        "]}",
                verifications.toJsonString());
    }
}
