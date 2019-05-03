package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "customer")
    private CustomerModel customer;

    @JsonCreator
    public CustomerRetrieveResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.customer = null;
    }

    @JsonCreator
    public CustomerRetrieveResponseModel(int resultCode, CustomerModel customer) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.customer = customer;
    }

    @JsonCreator
    public CustomerRetrieveResponseModel(int resultCode, String message, CustomerModel customer) {
        this.resultCode = resultCode;
        this.message = message;
        this.customer = customer;
    }

    @JsonProperty
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public CustomerModel getCustomer() {
        return customer;
    }
}
