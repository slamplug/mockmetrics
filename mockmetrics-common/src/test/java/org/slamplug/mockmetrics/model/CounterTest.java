package org.slamplug.mockmetrics.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.slamplug.mockmetrics.model.Counter.*;

public class CounterTest {

    @Test
    public void testParseCounterMetric() throws Exception {
        Counter counter = parse("test.metric:99|c");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
    }

    @Test
    public void testParseCounterMetricWithSampling() throws Exception {
        Counter counter = parse("test.metric:99|c|@0.1");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
        assertEquals(0.1, counter.getSampling(), 0.001);
    }

    @Test
    public void testToStringCounterMetric() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(99);
        assertEquals("test.metric:99|c", counter.toString());
    }

    @Test
    public void testToStringCounterMetricWithSampling() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(99).withSampling(0.1);
        assertEquals("test.metric:99|c|@0.1", counter.toString());
    }

    @Test
    public void testToJsonStringCounterMetric() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(99);
        assertEquals("{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":99}", counter.toJsonString());
    }

    @Test
    public void testToJsonStringCounterMetricWithSampling() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(99).withSampling(0.1);
        assertEquals("{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.1}", counter.toJsonString());
    }

    @Test
    public void testParseJsonStringCounterMetric() throws Exception {
        Counter counter = parseJson("{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":99}");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
        assertFalse(counter.hasSampling());
    }

    @Test
    public void testParseJsonStringCounterMetricWithSampling() throws Exception {
        Counter counter = parseJson("{\"type\":\"counter\",\"name\":\"test.metric\",\"value\":99,\"sampling\":0.1}");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
        assertTrue(counter.hasSampling());
        assertEquals(0.1, counter.getSampling(), 0.0001);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseCounterInvalidMetricString() throws Exception {
        parse("test.metric:aa|c");
    }
}
