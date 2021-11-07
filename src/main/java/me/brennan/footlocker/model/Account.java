package me.brennan.footlocker.model;

import me.brennan.footlocker.FootLocker;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class Account {
    private final String email, firstName, lastName, phoneNumber, password;

    public Account(String email, String firstName, String lastName, String phoneNumber, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public void write() {
        FootLocker.INSTANCE.getWriter().write(new String[]{email, firstName, lastName, phoneNumber, password});
    }

    public String getEmail() {
        return email;
    }
}
