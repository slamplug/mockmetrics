package org.slamplug.mockmetrics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Counter.class, name = "counter"),
        @JsonSubTypes.Type(value = Gauge.class, name = "gauge"),
        @JsonSubTypes.Type(value = Set.class, name = "set"),
        @JsonSubTypes.Type(value = Timer.class, name = "timer")
})
public interface Metric<T> {

    String getName();

    int getValue();

    Double getSampling();

    String toString();

    String toJsonString() throws JsonProcessingException;
}
