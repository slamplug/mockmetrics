package com.firstutility.mockmetrics.filter;

import com.firstutility.mockmetrics.model.Counter;
import com.firstutility.mockmetrics.model.Gauge;
import com.firstutility.mockmetrics.verify.Verification;
import com.firstutility.mockmetrics.verify.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.firstutility.mockmetrics.model.Counter.counter;
import static com.firstutility.mockmetrics.model.Gauge.gauge;
import static com.firstutility.mockmetrics.verify.Verification.verification;
import static com.firstutility.mockmetrics.verify.Verifications.verifications;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MetricFilterTest {

    private MetricFilter metricFilter;

    @Before
    public void createNewMetricFilter() {
        metricFilter = new MetricFilter();
    }

    @Test
    public void onMetricsNoMetricTest() throws Exception {
        assertEquals(0, metricFilter.depth());
    }

    @Test
    public void onMetricsSingleMetricTest() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        metricFilter.onMetric(counter);

        assertEquals(1, metricFilter.depth());
    }

    @Test
    public void onMetricsMultipleMetricsTestDifferentMetricNames() throws Exception {
        metricFilter.onMetrics(Arrays.asList(
                counter().withName("test.metric.1").withValue(100),
                counter().withName("test.metric.2").withValue(100)));

        assertEquals(2, metricFilter.depth());
    }

    @Test
    public void onMetricsMultipleMetricsTestSameMetricNames() throws Exception {
        metricFilter.onMetrics(Arrays.asList(
                counter().withName("test.metric.1").withValue(100),
                counter().withName("test.metric.1").withValue(100)));

        assertEquals(2, metricFilter.depth());
    }

    @Test
    public void clearMetricFilter() throws Exception {
        metricFilter.onMetrics(Arrays.asList(
                counter().withName("test.metric.1").withValue(100),
                counter().withName("test.metric.1").withValue(100)));

        assertEquals(2, metricFilter.depth());
        metricFilter.reset();
        assertEquals(0, metricFilter.depth());
    }

    @Test
    public void verifySingleMetricFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        metricFilter.onMetric(counter);

        try {
            metricFilter.verify(counter);
        } catch (AssertionError e) {
            fail("Assertion error should not be thrown, but was " + e.getMessage());
        }
    }

    @Test
    public void verifySingleMetricNotFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        try {
            metricFilter.verify(counter);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <1> time(s), expected:<test.metric:100|c> but was:<[]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationSingleMetricFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        Verification verification = verification().withMetric(counter);
        metricFilter.onMetric(counter);

        try {
            metricFilter.verify(verification);
        } catch (AssertionError e) {
            fail("Assertion error should not be thrown, but was " + e.getMessage());
        }
    }

    @Test
    public void verifyVerificationSingleMetricNotFoundInFilterQueue() throws Exception {
        Verification verification = verification().withMetric(
                counter().withName("test.metric").withValue(100));
        try {
            metricFilter.verify(verification);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <1> time(s), expected:<test.metric:100|c> but was:<[]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationSingleMetricMultipleTimesFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        Verification verification = verification().withMetric(counter).withMetric(counter).withTimes(2);

        metricFilter.onMetric(counter);
        metricFilter.onMetric(counter);
        try {
            metricFilter.verify(verification);
        } catch (AssertionError e) {
            fail("Assertion error should not be thrown, but was " + e.getMessage());
        }
    }

    @Test
    public void verifyVerificationSingleMetricMultipleTimesNotFoundInFilterQueue() throws Exception {
        Verification verification = verification().withMetric(
                counter().withName("test.metric").withValue(100)).withTimes(2);
        try {
            metricFilter.verify(verification);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <2> time(s), expected:<test.metric:100|c> but was:<[]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationSingleMetricMultipleTimesPartFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.metric").withValue(100);
        Verification verification = verification().withMetric(counter).withMetric(counter).withTimes(2);

        metricFilter.onMetric(counter);
        try {
            metricFilter.verify(verification);
        } catch (AssertionError e) {
            assertEquals("Metric not found <2> time(s), expected:<test.metric:100|c> but was:<[test.metric:100|c]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationsSingleMetricFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.counter").withValue(100);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter));
        metricFilter.onMetric(counter);

        try {
            metricFilter.verify(verifications);
        } catch (AssertionError e) {
            fail("Assertion error should not be thrown, but was " + e.getMessage());
        }
    }

    @Test
    public void verifyVerificationsMultipleMetricsFoundInFilterQueue() throws Exception {
        Counter counter = counter().withName("test.counter").withValue(100);
        Gauge gauge = gauge().withName("test.gauge").withValue(100);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter), verification().withMetric(gauge));
        metricFilter.onMetric(counter);
        metricFilter.onMetric(gauge);

        try {
            metricFilter.verify(verifications);
        } catch (AssertionError e) {
            fail("Assertion error should not be thrown, but was " + e.getMessage());
        }
    }

    @Test
    public void verifyVerificationsMultipleMetricsPartFound1InFilterQueue() throws Exception {
        Counter counter = counter().withName("test.counter").withValue(100);
        Gauge gauge = gauge().withName("test.gauge").withValue(100);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter), verification().withMetric(gauge));
        metricFilter.onMetric(counter);

        try {
            metricFilter.verify(verifications);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <1> time(s), expected:<test.gauge:100|g> but was:<[test.counter:100|c]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationsMultipleMetricsPartFound2InFilterQueue() throws Exception {
        Counter counter = counter().withName("test.counter").withValue(100);
        Gauge gauge = gauge().withName("test.gauge").withValue(100);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter), verification().withMetric(gauge));
        metricFilter.onMetric(gauge);

        try {
            metricFilter.verify(verifications);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <1> time(s), expected:<test.counter:100|c> but was:<[test.gauge:100|g]>", e.getMessage());
        }
    }

    @Test
    public void verifyVerificationsMultipleMetricsPartFound3InFilterQueue() throws Exception {
        Counter counter = counter().withName("test.counter").withValue(100);
        Gauge gauge = gauge().withName("test.gauge").withValue(100);
        Verifications verifications = verifications().withVerifications(
                verification().withMetric(counter).withTimes(2), verification().withMetric(gauge));
        metricFilter.onMetric(counter);
        metricFilter.onMetric(gauge);

        try {
            metricFilter.verify(verifications);
            fail("Assertion error should be thrown but was not");
        } catch (AssertionError e) {
            assertEquals("Metric not found <2> time(s), expected:<test.counter:100|c> but was:<[test.counter:100|c, test.gauge:100|g]>", e.getMessage());
        }
    }
}
