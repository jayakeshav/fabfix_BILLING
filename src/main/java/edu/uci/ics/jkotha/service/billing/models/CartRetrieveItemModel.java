package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartRetrieveItemModel {

    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = true)
    private String movieId;
    @JsonProperty(required = true)
    private int quantity;
    @JsonProperty(required = true)
    private float unitPrice;
    @JsonProperty(required = true)
    private float discount;
    @JsonProperty(required = true)
    private String title;

    @JsonCreator
    public CartRetrieveItemModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity,
            @JsonProperty(value = "unitPrice", required = true) float unitPrice,
            @JsonProperty(value = "discount", required = true) float discount,
            @JsonProperty(value = "title", required = true) String title) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.title = title;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty
    public float getUnitPrice() {
        return unitPrice;
    }

    @JsonProperty
    public float getDiscount() {
        return discount;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }
}
