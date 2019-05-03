package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonCreator
    public BasicResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
    }

    @JsonProperty
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }
}
