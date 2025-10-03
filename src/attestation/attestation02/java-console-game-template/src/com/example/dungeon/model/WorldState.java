package com.example.dungeon.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldState {
    private final Map<String, RoomState> rooms = new HashMap<>();

    public Map<String, RoomState> getRooms() {return rooms;}

    public static WorldState fromGameState(GameState gameState) {
        WorldState ws = new WorldState();
        // В нашем случае мир фиксированный — просто сохраняем состояние каждой комнаты
        // Для простоты обойдём все комнаты через известные связи
        collectRooms(gameState.getCurrent(), ws.rooms, new HashSet<>());
        return ws;
    }

    private static void collectRooms(Room room, Map<String, RoomState> map, Set<Room> visited) {
        if (room == null || visited.contains(room)) return;
        visited.add(room);
        map.put(room.getName(), new RoomState(room));
        for(Room r : room.getNeighbors().values())
            collectRooms(r, map, visited);
    }

    public void applyToGameState(GameState state) {
        // В этой реализации не восстанавливаем связи между комнатами — они фиксированы в bootstrapWorld()
        // Но восстанавливаем monster и items по имени комнаты
        Room current =  state.getCurrent();
        RoomState rs = rooms.get(current.getName());
        if (rs != null) {
            current.getMonsters().clear();
            current.getMonsters().addAll(rs.getMonsters());
            current.getItems().clear();
            current.getItems().addAll(rs.getItems());
        }
    }
}
