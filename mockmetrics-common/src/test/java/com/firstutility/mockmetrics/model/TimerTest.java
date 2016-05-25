package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Timer.parse;
import static com.firstutility.mockmetrics.model.Timer.timer;
import static org.junit.Assert.assertEquals;

public class TimerTest {

    @Test
    public void testParseTimerMetric() throws Exception {
        Timer timer = parse("test.metric:320|ms|@0.1");

        assertEquals("test.metric", timer.getName());
        assertEquals(320, timer.getValue());
        assertEquals(0.1, timer.getSampling(), 0.001);
    }

    @Test
    public void testToStringTimerMetric() throws Exception {
        Timer timer = timer().withName("test.metric").withValue(99);

        assertEquals("test.metric:99|ms|@0.0", timer.toString());
    }

    @Test
    public void testToStringTimerMetricWithSampling() throws Exception {
        Timer timer = timer().withName("test.metric").withValue(99).withSampling(0.01);

        assertEquals("test.metric:99|ms|@0.01", timer.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseTimerInvalidMetricString() throws Exception {
        parse("test.metric:aa|ms|@0.1");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseTimerInvalidMetricStringInvalidSampling() throws Exception {
        parse("test.metric:320|ms|@bb");
    }
}
