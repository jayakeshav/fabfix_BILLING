package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.*;

import java.text.DecimalFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemModel {

    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private int quantity;
    @JsonProperty(value = "unit_price", required = true)
    private float unit_price;
    @JsonProperty(value = "discount", required = true)
    private float discount;
    @JsonProperty(value = "saleDate", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "PST")
    private Date saleDate;

    @JsonCreator
    public ItemModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity,
            @JsonProperty(value = "unit_price", required = true) float unit_price,
            @JsonProperty(value = "discount", required = true) float discount,
            @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.unit_price = unit_price;
        this.discount = discount;
    }

    @JsonCreator
    public ItemModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity,
            @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
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
    public Date getSaleDate() {
        return saleDate;
    }

    @JsonProperty
    public float getUnit_price() {
        return unit_price;
    }

    @JsonProperty
    public float getDiscount() {
        return discount;
    }

    @JsonIgnore
    public String getTotal() {
        DecimalFormat df = new DecimalFormat("0.00");
        float res = quantity * (unit_price * (1 - (discount / 100)));
        return df.format(res);
    }
}
