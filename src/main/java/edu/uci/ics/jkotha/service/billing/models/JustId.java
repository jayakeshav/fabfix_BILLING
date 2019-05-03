package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JustId {
    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonCreator
    public JustId(
            @JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return id;
    }
}
