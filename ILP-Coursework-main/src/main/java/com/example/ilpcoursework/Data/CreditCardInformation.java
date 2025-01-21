package com.example.ilpcoursework.Data;

public class CreditCardInformation {

    private String creditCardNumber;
    private String creditCardExpiry;
    private String cvv;

    // Constructor
    public CreditCardInformation(String creditCardNumber, String creditCardExpiry, String cvv) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
    }

    // Getters and Setters for CreditCardInformation
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public void setCreditCardExpiry(String creditCardExpiry) {
        this.creditCardExpiry = creditCardExpiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}

