package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Set.parse;
import static org.junit.Assert.assertEquals;

public class SetTest {

    @Test
    public void testParseSetMetric() throws Exception {
        Set set = parse("test.metric:765|s");

        assertEquals("test.metric", set.getName());
        assertEquals(765, set.getValue());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseTimerInvalidMetricString() throws Exception {
        parse("test.metric:aa|s");
    }
}
