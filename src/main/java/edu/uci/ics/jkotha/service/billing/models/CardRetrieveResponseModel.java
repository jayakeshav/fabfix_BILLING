package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "creditcard")
    private CreditCardModel creditcard;

    @JsonCreator
    public CardRetrieveResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.creditcard = null;

    }

    @JsonCreator
    public CardRetrieveResponseModel(int resultCode, String message, CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = creditcard;
    }

    @JsonCreator
    public CardRetrieveResponseModel(int resultCode, CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.creditcard = creditcard;
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
    public CreditCardModel getCreditcard() {
        return creditcard;
    }
}
