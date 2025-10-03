package com.example.dungeon.model;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> neighbors = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private final List<Monster> monsters = new ArrayList<>();

    private Set<String> lockedExits = new HashSet<>();

    public boolean isExitLocked(String dir) {
        return lockedExits.contains(dir);
    }

    public void lockExit(String dir) {
        lockedExits.add(dir);
    }

    public void unlockExit(String direction) {
        lockedExits.remove(direction);
    }

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public Map<String, Room> getNeighbors() {
        return neighbors;
    }

    public List<Item> getItems() {
        return items;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + ": " + description);
        if (!items.isEmpty()) {
            sb.append("\nПредметы: ").append(String.join(", ", items.stream().map(Item::getName).toList()));
        }
        if (!monsters.isEmpty()) {
            sb.append("\nМонстры: ");
            sb.append(monsters.stream()
                    .map(m -> m.getName() + " (ур. " + m.getLevel() + ")")
                    .collect(Collectors.joining(", ")));
        }
        if (!neighbors.isEmpty()) {
            sb.append("\nВыходы: ").append(String.join(", ", neighbors.keySet()));
        }
        return sb.toString();
    }
}
