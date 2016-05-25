package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Gauge.parse;
import static org.junit.Assert.assertEquals;


public class GaugeTest {

    @Test
    public void testParseGaugeMetric() throws Exception {
        Gauge gauge = parse("test.metric:333|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(333, gauge.getValue());
    }

    @Test
    public void testParseGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = parse("test.metric:+10|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(10, gauge.getValue());
    }

    @Test
    public void testParseGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = parse("test.metric:-4|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(-4, gauge.getValue());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseGaugeInvalidMetricString() throws Exception {
        parse("test.metric:aa|g");
    }
}
