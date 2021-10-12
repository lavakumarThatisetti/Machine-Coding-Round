package com.lavakumar.trello.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BList {
    UUID id;
    String name;
    List<Card> cards;

    public BList(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.cards = new ArrayList<>();
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

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
