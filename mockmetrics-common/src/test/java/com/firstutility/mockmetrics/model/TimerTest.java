package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Timer.parse;
import static org.junit.Assert.assertEquals;

public class TimerTest {

    @Test
    public void testParseTimerMetric() throws Exception {
        Timer timer = parse("test.metric:320|ms|@0.1");

        assertEquals("test.metric", timer.getName());
        assertEquals(320, timer.getValue());
        assertEquals(0.1, timer.getSampling(), 0.001);
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
