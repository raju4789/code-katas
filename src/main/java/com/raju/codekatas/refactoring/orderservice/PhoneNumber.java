package com.raju.codekatas.refactoring.orderservice;

public class PhoneNumber implements CustomerContact {

    private final String phoneNumber;

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {

            throw new IllegalArgumentException("Invalid phone number");
        }
    }
}
