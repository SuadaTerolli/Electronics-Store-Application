package com.electronicstore.model;

public class Manager extends User {
    public Manager(int id, String name, String username, java.util.Date dateOfBirth, int phoneNumber, String email, double salary, String password, String accessLevel) {
        super(id, name, username, dateOfBirth, phoneNumber, email, salary, password, accessLevel);
    }
}