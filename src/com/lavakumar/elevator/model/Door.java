package com.lavakumar.elevator.model;

public class Door {

    boolean opened;

    public Door(boolean opened){
        this.opened = opened;
    }

    public void open(){
        this.opened = true;
    }

    public void close(){
        this.opened = false;
    }
    public boolean isOpened(){
        return opened;
    }


}
