package edu.uci.ics.jkotha.service.billing.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerModel {

    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "ccId", required = true)
    private String ccId;
    @JsonProperty(value = "address", required = true)
    private String address;

    @JsonCreator
    public CustomerModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "ccId", required = true) String ccId,
            @JsonProperty(value = "address", required = true) String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
    }

    @JsonProperty
    public String getEmail() {
        return email;
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
    public String getCcId() {
        return ccId;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }
}
