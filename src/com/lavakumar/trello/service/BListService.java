package com.lavakumar.trello.service;


import com.lavakumar.trello.model.Card;

import java.util.UUID;

public interface BListService {
    void createList(String name);
    void deleteList(UUID listId) throws Exception;
    void addCardToList(UUID listId, Card card);
}
