package org.slamplug.mockmetrics.model;

import org.junit.Test;

import static org.junit.Assert.*;


public class GaugeTest {

    @Test
    public void testParseGaugeMetric() throws Exception {
        Gauge gauge = Gauge.parse("test.metric:333|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(333, gauge.getValue());
        assertFalse(gauge.isIncrement());
    }

    @Test
    public void testParseGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = Gauge.parse("test.metric:+10|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(10, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test
    public void testParseGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = Gauge.parse("test.metric:-4|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(-4, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test
    public void testToStringGaugeMetric() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(1);
        assertEquals("test.metric:1|g", gauge.toString());
    }

    @Test
    public void testToStringGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(-4).withIncrement();
        assertEquals("test.metric:-4|g", gauge.toString());
    }

    @Test
    public void testToStringGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(2).withIncrement();
        assertEquals("test.metric:+2|g", gauge.toString());
    }

    @Test
    public void testToJsonStringGaugeMetric() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(1);
        assertEquals("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":1,\"increment\":false}", gauge.toJsonString());
    }

    @Test
    public void testToJsonStringGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(-4).withIncrement();
        assertEquals("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":-4,\"increment\":true}", gauge.toJsonString());
    }

    @Test
    public void testToJsonStringGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = Gauge.gauge().withName("test.metric").withValue(2).withIncrement();
        assertEquals("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":2,\"increment\":true}", gauge.toJsonString());
    }

    @Test
    public void testParseJsonStringGaugeMetric() throws Exception {
        Gauge gauge = Gauge.parseJson("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":1,\"increment\":false}");

        assertEquals("test.metric", gauge.getName());
        assertEquals(1, gauge.getValue());
        assertFalse(gauge.isIncrement());
    }

    @Test
    public void testParseJsonStringGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = Gauge.parseJson("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":-4,\"increment\":true}");

        assertEquals("test.metric", gauge.getName());
        assertEquals(-4, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test
    public void testParseJsonStringGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = Gauge.parseJson("{\"type\":\"gauge\",\"name\":\"test.metric\",\"value\":2,\"increment\":true}");

        assertEquals("test.metric", gauge.getName());
        assertEquals(2, gauge.getValue());
        assertTrue(gauge.isIncrement());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseGaugeInvalidMetricString() throws Exception {
        Gauge.parse("test.metric:aa|g");
    }
}
