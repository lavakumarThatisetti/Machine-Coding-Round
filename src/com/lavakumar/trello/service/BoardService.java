package com.lavakumar.trello.service;

import com.lavakumar.trello.model.BList;
import com.lavakumar.trello.model.User;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    UUID createBoard(String name);
    void deleteBoard(UUID boardId) throws Exception;
    void addUsersToBoard(UUID boardId, List<User> users);
    void addUserToBoard(UUID boardId, User user);
    void addListToBoard(UUID boardId, BList bList);

    void getBoard(UUID boardId);
}
