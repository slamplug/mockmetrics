package com.firstutility.mockmetrics.verify;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.firstutility.mockmetrics.utils.JsonFilterHelper;

import java.util.ArrayList;
import java.util.List;

@JsonFilter("verificationsJsonFilter")
public class Verifications {

    private final List<Verification> verifications;

    public Verifications() {
        verifications = new ArrayList<>();
    }

    public static Verifications verifications() {
        return new Verifications();
    }

    public Verifications withVerifications(final Verification... verifications) {
        for (Verification verification : verifications) {
            this.verifications.add(verification);
        }
        return this;
    }

    public List<Verification> getVerifications() {
        return verifications;
    }

    public String toJsonString() throws JsonProcessingException {
        SimpleFilterProvider simpleFilterProvider = null;
        simpleFilterProvider = JsonFilterHelper.addFilters(this);
        int i = 2;
        return new ObjectMapper().setFilterProvider(simpleFilterProvider)
                .writeValueAsString(this);
    }
}
