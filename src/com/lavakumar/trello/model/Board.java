package com.lavakumar.trello.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Board {
    UUID id;
    String name;
    PRIVACY privacy;
    String url;
    List<User> users;
    List<BList> lists;

    public Board(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.url = "boards/"+this.id;
        this.privacy = PRIVACY.PUBLIC;
        this.users =  new ArrayList<>();
        this.lists =  new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PRIVACY getPrivacy() {
        return privacy;
    }

    public void setPrivacy(PRIVACY privacy) {
        this.privacy = privacy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<BList> getLists() {
        return lists;
    }

    public void setLists(List<BList> lists) {
        this.lists = lists;
    }
}
