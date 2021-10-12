package com.lavakumar.trello.model;

import java.util.UUID;

public class Card {
    UUID id;
    String name;
    String description;
    User assignedUser;

    public Card(String name,String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.assignedUser = null;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }
}
