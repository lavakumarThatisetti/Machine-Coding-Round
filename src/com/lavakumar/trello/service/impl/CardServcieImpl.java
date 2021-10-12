package com.lavakumar.trello.service.impl;

import com.lavakumar.trello.model.Card;
import com.lavakumar.trello.model.User;
import com.lavakumar.trello.service.CardService;

import java.util.HashMap;
import java.util.UUID;

public class CardServcieImpl implements CardService {
    HashMap<UUID, Card> listOfCards;
    public CardServcieImpl(){
        listOfCards = new HashMap<>();
    }
    @Override
    public void createCard(String name,String description) {
        Card card = new Card(name,description);
        this.listOfCards.put(card.getId(),card);
    }

    @Override
    public void deleteCard(UUID cardId) throws Exception {
        if(listOfCards.get(cardId)!=null) listOfCards.remove(cardId);
        else throw new Exception("Card "+cardId+" Not preset in List of Cards");
    }

    @Override
    public void assignCardTOUser(UUID cardId, User user) {
        Card card =  listOfCards.get(cardId);
        if(card!=null){
            card.setAssignedUser(user);
        }
    }
}
