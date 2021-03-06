package org.slamplug.mockmetrics.utils;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slamplug.mockmetrics.model.Counter;
import org.slamplug.mockmetrics.model.Metric;
import org.slamplug.mockmetrics.verify.Verification;
import org.slamplug.mockmetrics.verify.Verifications;
import com.google.common.collect.Sets;

import java.util.*;

public class JsonFilterHelper {

    private static Map<String, Set<String>> jsonFilterMap = new HashMap<String, Set<String>>() {{
        put("gaugeJsonFilter", Sets.newHashSet(Arrays.asList("name", "value", "increment")));
        put("setJsonFilter", Sets.newHashSet(Arrays.asList("name", "value")));
        put("counterJsonFilter", Sets.newHashSet(Arrays.asList("name", "value")));
        put("timerJsonFilter", Sets.newHashSet(Arrays.asList("name", "value", "sampling")));
        put("verificationJsonFilter", Sets.newHashSet(Arrays.asList("metric", "times")));
        put("verificationsJsonFilter", Sets.newHashSet(Arrays.asList("verifications")));
    }};

    public static synchronized SimpleFilterProvider addFilters(final Object object) {
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();

        if (object instanceof Metric) {
            addFilter(object, simpleFilterProvider);
        } else if (object instanceof Verification) {
            addFilter(object, simpleFilterProvider);
            addFilter(((Verification) object).getMetric(), simpleFilterProvider);
        } else if (object instanceof Verifications) {
            addFilter(object, simpleFilterProvider);
            for (Verification verification : ((Verifications) object).getVerifications()) {
                addFilter(verification, simpleFilterProvider);
                addFilter(verification.getMetric(), simpleFilterProvider);
            }
        }

        return simpleFilterProvider;
    }

    private static SimpleFilterProvider addFilter(final Object object, final SimpleFilterProvider simpleFilterProvider) {
        String filterName = getFilterName(object.getClass());
        simpleFilterProvider.addFilter(filterName,
                SimpleBeanPropertyFilter.filterOutAllExcept(
                        modifyIncludeFieldSet(object, new HashSet<>(jsonFilterMap.get(filterName)))));
        return simpleFilterProvider;
    }

    private static Set<String> modifyIncludeFieldSet(final Object object, final Set<String> includeFields) {
        if (object instanceof Counter) {
            Counter counter = (Counter) object;
            if (counter.getSampling() != null) {
                includeFields.add("sampling");
            }
        }
        return includeFields;
    }

    private static String getFilterName(final Class clazz) {
        return clazz.getSimpleName().toLowerCase() + "JsonFilter";
    }
}
