package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paypal.api.payments.Amount;

public class TransactionModel {
    @JsonProperty(value = "transactionId", required = true)
    private String transactionId;
    @JsonProperty(value = "state", required = true)
    private String state;
    @JsonProperty(value = "amount", required = true)
    private AmountModel amount;
    @JsonProperty(value = "transaction_fee", required = true)
    private TransactionFee transaction_fee;
    @JsonProperty(value = "create_time", required = true)
    private String create_time;
    @JsonProperty(value = "update_time", required = true)
    private String update_time;
    @JsonProperty(value = "items", required = true)
    private ItemModel[] items;

    @JsonCreator

    public TransactionModel(
            @JsonProperty(value = "transactionId", required = true) String transactionId,
            @JsonProperty(value = "state", required = true) String state,
            @JsonProperty(value = "amount", required = true) Amount amount,
            @JsonProperty(value = "transaction_fee", required = true) TransactionFee transaction_fee,
            @JsonProperty(value = "create_time", required = true) String create_time,
            @JsonProperty(value = "update_time", required = true) String update_time,
            @JsonProperty(value = "items", required = true) ItemModel[] items) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = new AmountModel(amount.getTotal(), amount.getCurrency());
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    @JsonProperty
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty
    public String getState() {
        return state;
    }

    @JsonProperty
    public AmountModel getAmount() {
        return amount;
    }

    @JsonProperty
    public TransactionFee getTransaction_fee() {
        return transaction_fee;
    }

    @JsonProperty
    public String getCreate_time() {
        return create_time;
    }

    @JsonProperty
    public String getUpdate_time() {
        return update_time;
    }

    @JsonProperty
    public ItemModel[] getItems() {
        return items;
    }

    @JsonIgnore
    public void setItems(ItemModel[] items) {
        this.items = items;
    }
}
