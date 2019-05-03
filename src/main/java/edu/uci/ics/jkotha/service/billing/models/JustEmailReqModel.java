package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JustEmailReqModel {
    @JsonProperty
    private String email;

    @JsonCreator
    public JustEmailReqModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }
}
