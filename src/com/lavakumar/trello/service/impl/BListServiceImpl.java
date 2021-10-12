package com.lavakumar.trello.service.impl;

import com.lavakumar.trello.model.BList;
import com.lavakumar.trello.model.Board;
import com.lavakumar.trello.model.Card;
import com.lavakumar.trello.service.BListService;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BListServiceImpl implements BListService {
    HashMap<UUID, BList> bLists;
    public BListServiceImpl(){
        bLists = new HashMap<>();
    }
    @Override
    public void createList(String name) {
        BList list = new BList(name);
        this.bLists.put(list.getId(),list);
    }

    @Override
    public void deleteList(UUID listId) throws Exception {
        if(bLists.get(listId)!=null) bLists.remove(listId);
        else throw new Exception("ListId "+listId+" Not preset in Lists of Board");
    }


    @Override
    public void addCardToList(UUID listId, Card card) {
        BList bList = bLists.get(listId);
        if(bList!=null){
            List<Card> cards = bList.getCards();
            cards.add(card);
            bList.setCards(cards);
        }
        bLists.put(listId,bList);
    }
}
