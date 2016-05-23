package com.firstutility.mockmetrics.handler;

import com.firstutility.mockmetrics.model.Metric;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetricHandlerTest {

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
