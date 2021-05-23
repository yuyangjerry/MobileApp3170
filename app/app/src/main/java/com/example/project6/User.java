package com.example.project6;

import java.util.UUID;

/**
 * This is a simple class that represents a user
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private UUID uuid;

    /**
     * Instantiate a User
     *
     * @param firstName the user first name
     * @param lastName the user last name
     * @param email the user email
     * @param uuid the user uuid
     */
    public User(String firstName, String lastName, String email, UUID uuid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uuid = uuid;
    }

    /**
     *
     * @return user first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return user last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @return user email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return user uuid
     */
    public UUID getUuid() {
        return uuid;
    }
}
