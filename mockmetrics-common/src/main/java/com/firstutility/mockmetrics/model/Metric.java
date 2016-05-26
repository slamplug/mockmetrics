package com.firstutility.mockmetrics.model;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Metric {

    String getName();

    int getValue();

    double getSampling();

    String toString();

    String toJsonString() throws JsonProcessingException;
}
