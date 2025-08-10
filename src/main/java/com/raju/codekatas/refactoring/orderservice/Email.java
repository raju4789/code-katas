package com.raju.codekatas.refactoring.orderservice;

public class Email implements CustomerContact {

    private final String emailId;

    public Email(String emailId) {
        this.emailId = emailId;
    }


    @Override
    public void validate() throws IllegalArgumentException {
        if (emailId == null || !emailId.contains("@") || !emailId.endsWith(".com")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
