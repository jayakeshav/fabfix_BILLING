package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartDeleteReqModel {
    private String email;
    private String movieId;

    @JsonCreator
    public CartDeleteReqModel(@JsonProperty(value = "email", required = true) String email,
                              @JsonProperty(value = "movieId", required = true) String movieId) {
        this.email = email;
        this.movieId = movieId;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getMovieId() {
        return movieId;
    }
}
