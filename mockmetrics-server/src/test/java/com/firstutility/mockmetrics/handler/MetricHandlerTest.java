package com.firstutility.mockmetrics.handler;

import com.firstutility.mockmetrics.model.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetricHandlerTest {

    @Test
    public void testParseCounterMetric() throws Exception {
        Counter counter = MetricHandler.parseCounter("test.metric:99|c");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
    }

    @Test
    public void testParseCounterMetricWithSampling() throws Exception {
        Counter counter = MetricHandler.parseCounter("test.metric:99|c|@0.1");

        assertEquals("test.metric", counter.getName());
        assertEquals(99, counter.getValue());
        assertEquals(0.1, counter.getSampling(), 0.001);
    }

    @Test
    public void testParseGaugeMetric() throws Exception {
        Gauge gauge = MetricHandler.parseGauge("test.metric:333|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(333, gauge.getValue());
    }

    @Test
    public void testParseGaugeMetricPositiveIncrement() throws Exception {
        Gauge gauge = MetricHandler.parseGauge("test.metric:+10|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(10, gauge.getValue());
    }

    @Test
    public void testParseGaugeMetricNegativeIncrement() throws Exception {
        Gauge gauge = MetricHandler.parseGauge("test.metric:-4|g");

        assertEquals("test.metric", gauge.getName());
        assertEquals(-4, gauge.getValue());
    }

    @Test
    public void testParseTimerMetric() throws Exception {
        Timer timer = MetricHandler.parseTimer("test.metric:320|ms|@0.1");

        assertEquals("test.metric", timer.getName());
        assertEquals(320, timer.getValue());
        assertEquals(0.1, timer.getSampling(), 0.001);
    }

    @Test
    public void testParseSetMetric() throws Exception {
        Set set = MetricHandler.parseSet("test.metric:765|s");

        assertEquals("test.metric", set.getName());
        assertEquals(765, set.getValue());
    }

    @Test
    public void testParseMultipleCounterMetric() throws Exception {
        List<Metric> metricList = MetricHandler.handleMetrics("test.metric.1:99|c\ntest.metric.2:66|c".split("\n"));

        assertEquals(2, metricList.size());
        assertEquals("test.metric.1", metricList.get(0).getName());
        assertEquals(99, metricList.get(0).getValue());
        assertEquals("test.metric.2", metricList.get(1).getName());
        assertEquals(66, metricList.get(1).getValue());
    }
}
