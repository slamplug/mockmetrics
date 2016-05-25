package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Gauge.gauge;
import static com.firstutility.mockmetrics.model.Gauge.parse;
import static org.junit.Assert.*;


public class GaugeTest {

    @Test
    public void testParseGaugeMetric() throws Exception {
        Gauge gauge = parse("test.metric:333|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(333, gauge.getValue());
        assertFalse(gauge.isIncrement());
    }

    @Test
    public void testParseGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = parse("test.metric:+10|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(10, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test
    public void testParseGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = parse("test.metric:-4|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(-4, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test
    public void testToStringGaugeMetric() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(1);
        assertEquals("test.metric:1|g", gauge.toString());
    }

    @Test
    public void testToStringGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(-4).withIncrement();
        assertEquals("test.metric:-4|g", gauge.toString());
    }

    @Test
    public void testToStringGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = gauge().withName("test.metric").withValue(2).withIncrement();
        assertEquals("test.metric:+2|g", gauge.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseGaugeInvalidMetricString() throws Exception {
        parse("test.metric:aa|g");
    }
}
