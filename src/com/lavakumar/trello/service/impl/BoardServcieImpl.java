package com.lavakumar.trello.service.impl;

import com.lavakumar.trello.model.BList;
import com.lavakumar.trello.model.Board;
import com.lavakumar.trello.model.User;
import com.lavakumar.trello.service.BoardService;

import java.util.*;

public class BoardServcieImpl implements BoardService {
    HashMap<UUID,Board> boards;
    public BoardServcieImpl(){
        boards = new HashMap<>();
    }

    @Override
    public UUID createBoard(String name) {
        Board board = new Board(name);
        this.boards.put(board.getId(),board);
        return board.getId();
    }

    @Override
    public void deleteBoard(UUID boardId) throws Exception {
        if(boards.get(boardId)!=null) boards.remove(boardId);
        else throw new Exception("Board "+boardId+" Not preset in List of Boards");
    }

    @Override
    public void addUsersToBoard(UUID boardId, List<User> users) {
        Board board = boards.get(boardId);
        if(board!=null){
            List<User> boardUsers = board.getUsers();
            boardUsers.addAll(users);
            board.setUsers(users);
        }
        boards.put(boardId,board);
    }

    @Override
    public void addUserToBoard(UUID boardId, User user) {
        Board board = boards.get(boardId);
        if(board!=null){
            List<User> users = board.getUsers();
            users.add(user);
            board.setUsers(users);
        }
        boards.put(boardId,board);
    }

    @Override
    public void addListToBoard(UUID boardId, BList bList) {
        Board board = boards.get(boardId);
        if(board!=null){
            List<BList> bLists = board.getLists();
            bLists.add(bList);
            board.setLists(bLists);
        }
        boards.put(boardId,board);
    }

    @Override
    public void getBoard(UUID boardId) {
        Board board = boards.get(boardId);
        System.out.println(" Board Id "+board.getId());
        board.getLists().forEach(
                bList -> {
                    StringBuilder str = new StringBuilder();
                    str.append(" List Name: ").append(bList.getName()).append("\n");
                    bList.getCards().forEach(
                            card -> {
                                str.append(" \n Card Name: ").
                                        append(card.getName()).
                                        append(" Card Description: ").append(card.getDescription())
                                        .append(" Assigned User : ").append(
                                                card.getAssignedUser()!=null?card.getAssignedUser().getUserName():" Not Assigned"
                                        );
                            }
                    );
                    if(bList.getCards().size()==0) str.append(" Empty List: ");
                    System.out.println(str.toString());
                }
        );
    }
}
