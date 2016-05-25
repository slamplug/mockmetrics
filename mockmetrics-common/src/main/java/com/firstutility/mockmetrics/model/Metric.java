package com.firstutility.mockmetrics.model;

public interface Metric {

    String getName();

    int getValue();

    double getSampling();

    String toString();
}
