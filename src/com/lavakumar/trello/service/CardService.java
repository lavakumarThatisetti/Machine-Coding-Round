package com.lavakumar.trello.service;

import com.lavakumar.trello.model.User;

import java.util.UUID;

public interface CardService {
    void createCard(String name,String description);
    void deleteCard(UUID cardId) throws Exception;
    void assignCardTOUser(UUID cardId, User user);
}
