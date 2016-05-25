package com.firstutility.mockmetrics.model;

import org.junit.Test;

import static com.firstutility.mockmetrics.model.Set.parse;
import static com.firstutility.mockmetrics.model.Set.set;
import static org.junit.Assert.assertEquals;

public class SetTest {

    @Test
    public void testParseSetMetric() throws Exception {
        Set set = parse("test.metric:765|s");

        assertEquals("test.metric", set.getName());
        assertEquals(765, set.getValue());
    }

    @Test
    public void tesToStringSetMetric() throws Exception {
        Set set = set().withName("test.metric").withValue(99);

        assertEquals("test.metric:99|s", set.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testParseTimerInvalidMetricString() throws Exception {
        parse("test.metric:aa|s");
    }
}
