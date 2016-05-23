package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Counter.parse;
import static org.junit.Assert.assertEquals;

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

    @Test(expected = NumberFormatException.class)
    public void testParseCounterInvalidMetricString() throws Exception {
        parse("test.metric:aa|c");
    }
}
