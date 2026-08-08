package com.electronicstore.controller;

import com.electronicstore.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LogInController {

    private List<User> users = new ArrayList<>();

    public void loadUsersFromFile(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    String name = parts[1].trim();
                    String username = parts[2].trim();
                    String password = parts[7].trim();
                    String role = parts[8].trim();
                    users.add(new User(name, username, password, role));
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Error loading users from resource: " + resourcePath);
        }
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
