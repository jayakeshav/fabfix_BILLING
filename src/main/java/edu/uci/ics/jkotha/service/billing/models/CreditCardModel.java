package edu.uci.ics.jkotha.service.billing.models;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "expiration", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "PST")
    private Date expiration;

    @JsonCreator
    public CreditCardModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "expiration", required = true) Date expiration) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty
    public String getLastName() {
        return lastName;
    }

    @JsonProperty
    public Date getExpiration() {
        return expiration;
    }
}
