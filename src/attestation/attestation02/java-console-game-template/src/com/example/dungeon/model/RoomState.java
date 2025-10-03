package com.example.dungeon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomState {
    private String roomName;
    private List<Monster> monsters = new ArrayList<>();
    private List<Item> items =  new ArrayList<Item>();

    public RoomState() {}           //для десериализации

    public RoomState(Room room) {
        this.roomName = room.getName();
        this.monsters = new ArrayList<>(room.getMonsters());
        this.items = new ArrayList<>(room.getItems());
    }

    //гетеры
    public  String getName() {return roomName;}
    public List<Monster> getMonsters() {return monsters;}
    public  List<Item> getItems() {return items;}
}
