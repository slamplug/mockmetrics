package org.slamplug.mockmetrics.verify;

import org.slamplug.mockmetrics.model.Counter;
import org.slamplug.mockmetrics.model.Gauge;
import org.slamplug.mockmetrics.model.Set;
import org.slamplug.mockmetrics.model.Timer;
import org.junit.Test;

import static org.junit.Assert.*;

public class VerificationTest {

    @Test
    public void testToJsonStringVerificationWithCounter() throws Exception {
        Counter counter = Counter.counter().withName("test.metric").withValue(1);
        Verification verification = Verification.verification().withMetric(counter);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithCounterWithSampling() throws Exception {
        Counter counter = Counter.counter().withName("test.metric").withValue(1).withSampling(0.1);
        Verification verification = Verification.verification().withMetric(counter);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithCounterWithSamplingWithTimes() throws Exception {
        Counter counter = Counter.counter().withName("test.metric").withValue(1).withSampling(0.1);
        Verification verification = Verification.verification().withMetric(counter).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.1},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGauge() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(99);
        Verification verification = Verification.verification().withMetric(gauge);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":false},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGaugeWithIncrement() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(99).withIncrement();
        Verification verification = Verification.verification().withMetric(gauge);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":true},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGaugeWithIncrementWithTimes() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(99).withIncrement();
        Verification verification = Verification.verification().withMetric(gauge).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":true},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithSet() throws Exception {
        Set set = Set.set().withName("test.metric").withValue(99);
        Verification verification = Verification.verification().withMetric(set);

        assertEquals("{\"metric\":{\"type\":\"set\",\"name\":\"test.metric\",\"value\":99},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithSetWithTimes() throws Exception {
        Set set = Set.set().withName("test.metric").withValue(99);
        Verification verification = Verification.verification().withMetric(set).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"set\",\"name\":\"test.metric\",\"value\":99},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimer() throws Exception {
        Timer timer = Timer.timer().withName("test.metric").withValue(99);
        Verification verification = Verification.verification().withMetric(timer);

        assertEquals("{\"metric\":{\"type\":\"timer\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimerWithSampling() throws Exception {
        Timer timer = Timer.timer().withName("test.metric").withValue(99).withSampling(0.2);
        Verification verification = Verification.verification().withMetric(timer);

        assertEquals("{\"metric\":{\"type\":\"timer\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.2},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimerWithSamplingWithTimes() throws Exception {
        Timer timer = Timer.timer().withName("test.metric").withValue(99).withSampling(0.2);
        Verification verification = Verification.verification().withMetric(timer).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"timer\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.2},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testParseJsonStringVerificationWithCounter() throws Exception {
        Verification verification = Verification.parseJson(
                "{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1},\"times\":1}");

        assertEquals(1, verification.getTimes());
        assertTrue(verification.getMetric() instanceof Counter);
        Counter counter = (Counter) verification.getMetric();
        assertEquals("test.metric", counter.getName());
        assertEquals(1, counter.getValue());
        assertFalse(counter.hasSampling());
    }

    @Test
    public void testParseJsonStringVerificationWithCounterWithSampling() throws Exception {
        Verification verification = Verification.parseJson(
                "{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.2},\"times\":1}");

        assertEquals(1, verification.getTimes());
        assertTrue(verification.getMetric() instanceof Counter);
        Counter counter = (Counter) verification.getMetric();
        assertEquals("test.metric", counter.getName());
        assertEquals(1, counter.getValue());
        assertTrue(counter.hasSampling());
        assertEquals(0.2, counter.getSampling(), 0.001);
    }

    @Test
    public void testParseJsonStringVerificationWithCounterWithSamplingWithTimes() throws Exception {
        Verification verification = Verification.parseJson(
                "{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.2},\"times\":3}");

        assertEquals(3, verification.getTimes());
        assertTrue(verification.getMetric() instanceof Counter);
        Counter counter = (Counter) verification.getMetric();
        assertEquals("test.metric", counter.getName());
        assertEquals(1, counter.getValue());
        assertTrue(counter.hasSampling());
        assertEquals(0.2, counter.getSampling(), 0.001);
    }
}
