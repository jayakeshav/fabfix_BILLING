package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "items")
    private ItemModel[] items;

    @JsonCreator
    public OrderRetrieveResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        items = null;
    }

    @JsonCreator
    public OrderRetrieveResponseModel(int resultCode, ItemModel[] items) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.items = items;
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
    public ItemModel[] getItems() {
        return items;
    }
}
