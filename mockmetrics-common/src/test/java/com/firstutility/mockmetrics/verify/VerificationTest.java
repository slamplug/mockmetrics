package com.firstutility.mockmetrics.verify;

import com.firstutility.mockmetrics.model.Counter;
import com.firstutility.mockmetrics.model.Gauge;
import com.firstutility.mockmetrics.model.Set;
import com.firstutility.mockmetrics.model.Timer;
import org.junit.Test;

import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Gauge.gauge;
import static com.firstutility.mockmetrics.model.Set.set;
import static com.firstutility.mockmetrics.model.Timer.timer;
import static com.firstutility.mockmetrics.verify.Verification.verification;
import static org.junit.Assert.*;

public class VerificationTest {

    @Test
    public void testToJsonStringVerificationWithCounter() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(1);
        Verification verification = verification().withMetric(counter);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithCounterWithSampling() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(1).withSampling(0.1);
        Verification verification = verification().withMetric(counter);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithCounterWithSamplingWithTimes() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(1).withSampling(0.1);
        Verification verification = verification().withMetric(counter).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":1,\"sampling\":0.1},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGauge() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(99);
        Verification verification = verification().withMetric(gauge);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":false},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGaugeWithIncrement() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(99).withIncrement();
        Verification verification = verification().withMetric(gauge);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":true},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithGaugeWithIncrementWithTimes() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(99).withIncrement();
        Verification verification = verification().withMetric(gauge).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":99,\"increment\":true},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithSet() throws Exception {
        Set set = set().withName("test.metric").withValue(99);
        Verification verification = verification().withMetric(set);

        assertEquals("{\"metric\":{\"type\":\"set\",\"name\":\"test.metric\",\"value\":99},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithSetWithTimes() throws Exception {
        Set set = set().withName("test.metric").withValue(99);
        Verification verification = verification().withMetric(set).withTimes(2);

        assertEquals("{\"metric\":{\"type\":\"set\",\"name\":\"test.metric\",\"value\":99},\"times\":2}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimer() throws Exception {
        Timer timer = timer().withName("test.metric").withValue(99);
        Verification verification = verification().withMetric(timer);

        assertEquals("{\"metric\":{\"type\":\"timer\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.1},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimerWithSampling() throws Exception {
        Timer timer = timer().withName("test.metric").withValue(99).withSampling(0.2);
        Verification verification = verification().withMetric(timer);

        assertEquals("{\"metric\":{\"type\":\"timer\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.2},\"times\":1}",
                verification.toJsonString());
    }

    @Test
    public void testToJsonStringVerificationWithTimerWithSamplingWithTimes() throws Exception {
        Timer timer = timer().withName("test.metric").withValue(99).withSampling(0.2);
        Verification verification = verification().withMetric(timer).withTimes(2);

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
