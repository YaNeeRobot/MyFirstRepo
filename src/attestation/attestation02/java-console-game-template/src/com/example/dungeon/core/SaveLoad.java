package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SaveLoad {
    private static final Path SAVE = Paths.get("save.txt");
    private static final Path SCORES = Paths.get("scores.csv");

    public static void save(GameState s) {
        try (BufferedWriter w = Files.newBufferedWriter(SAVE)) {
            Player p = s.getPlayer();
            // 1. Сохраняем игрока
            w.write("player;" + p.getName() + ";" + p.getHp() + ";" + p.getAttack());
            w.newLine();

            // 2. Сохраняем инвентарь
            String inv = p.getInventory().stream()
                    .map(i -> i.getClass().getSimpleName() + ":" + i.getName())
                    .collect(Collectors.joining(","));
            w.write("inventory;" + inv);
            w.newLine();

            // 3. Сохраняем имя текущей комнаты
            w.write("room;" + s.getCurrent().getName());
            w.newLine();

            // ➕ 4. СОХРАНЯЕМ МОНСТРОВ И ПРЕДМЕТЫ КОМНАТЫ
            Room currentRoom = s.getCurrent();
            // Сохраняем всех монстров через | (если список monsters существует)
            String roomMonsters = currentRoom.getMonsters().stream()
                    .map(m -> m.getName() + ";" + m.getLevel() + ";" + m.getHp())
                    .collect(Collectors.joining("|"));
            w.write("room-monsters;" + roomMonsters);
            w.newLine();

            String roomItems = currentRoom.getItems().stream()
                    .map(i -> i.getClass().getSimpleName() + ":" + i.getName())
                    .collect(Collectors.joining(","));
            w.write("room-items;" + roomItems);
            w.newLine();

            // 5. Сохраняем счёт (уже есть)
            System.out.println("Сохранено в " + SAVE.toAbsolutePath());
            writeScore(p.getName(), s.getScore());
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить игру", e);
        }
    }

    public static void load(GameState s) {
        if (!Files.exists(SAVE)) {
            System.out.println("Сохранение не найдено.");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(SAVE)) {
            Map<String, String> map = new HashMap<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }

            // 1. Восстанавливаем игрока
            Player p = s.getPlayer();
            String playerData = map.get("player");
            if (playerData != null) {
                String[] pp = playerData.split(";");
                if (pp.length >= 4) {
                    p.setName(pp[1]);
                    p.setHp(Integer.parseInt(pp[2]));
                    p.setAttack(Integer.parseInt(pp[3]));
                }
            }

            // 2. Восстанавливаем инвентарь
            p.getInventory().clear();
            String invData = map.get("inventory");
            if (invData != null && !invData.isEmpty()) {
                for (String tok : invData.split(",")) {
                    String[] t = tok.split(":", 2);
                    if (t.length < 2) continue;
                    switch (t[0]) {
                        case "Potion" -> p.getInventory().add(new Potion(t[1], 5));
                        case "Key" -> p.getInventory().add(new Key(t[1]));
                        case "Weapon" -> p.getInventory().add(new Weapon(t[1], 3));
                    }
                }
            }

            // 3. Восстанавливаем текущую комнату по имени
            String roomName = map.get("room");
            if (roomName != null) {
                // Мир уже создан в bootstrapWorld(), ищем комнату по имени
                // Для простоты — поддерживаем только известные комнаты
                Room targetRoom = findRoomByName(s.getCurrent(), roomName);
                if (targetRoom != null) {
                    s.setCurrent(targetRoom);
                } else {
                    System.out.println("Комната '" + roomName + "' не найдена. Остаёмся в начальной.");
                }
            }

            // 4. Восстанавливаем монстров в текущей комнате
            Room current = s.getCurrent();
            current.getMonsters().clear(); // очищаем список
            String roomMonsters = map.get("room-monsters");
            if (roomMonsters != null && !roomMonsters.isEmpty()) {
                for (String mStr : roomMonsters.split("\\|")) {
                    String[] m = mStr.split(";");
                    if (m.length >= 3) {
                        current.getMonsters().add(new Monster(m[0], Integer.parseInt(m[1]), Integer.parseInt(m[2])));
                    }
                }
            }

            // 5. Восстанавливаем предметы в текущей комнате
            current.getItems().clear();
            String roomItems = map.get("room-items");
            if (roomItems != null && !roomItems.isEmpty()) {
                for (String tok : roomItems.split(",")) {
                    String[] t = tok.split(":", 2);
                    if (t.length < 2) continue;
                    switch (t[0]) {
                        case "Potion" -> current.getItems().add(new Potion(t[1], 5));
                        case "Key" -> current.getItems().add(new Key(t[1]));
                        case "Weapon" -> current.getItems().add(new Weapon(t[1], 3));
                    }
                }
            }

            System.out.println("Игра загружена.");
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось загрузить игру", e);
        }
    }

    public static void printScores() {
        if (!Files.exists(SCORES)) {
            System.out.println("Пока нет результатов.");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(SCORES)) {
            System.out.println("Таблица лидеров (топ-10):");
            r.lines().skip(1).map(l -> l.split(",")).map(a -> new Score(a[1], Integer.parseInt(a[2])))
                    .sorted(Comparator.comparingInt(Score::score).reversed()).limit(10)
                    .forEach(s -> System.out.println(s.player() + " — " + s.score()));
        } catch (IOException e) {
            System.err.println("Ошибка чтения результатов: " + e.getMessage());
        }
    }

    private static void writeScore(String player, int score) {
        try {
            boolean header = !Files.exists(SCORES);
            try (BufferedWriter w = Files.newBufferedWriter(SCORES, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (header) {
                    w.write("ts,player,score");
                    w.newLine();
                }
                w.write(LocalDateTime.now() + "," + player + "," + score);
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать очки: " + e.getMessage());
        }
    }

    private record Score(String player, int score) {
    }

    private static Room findRoomByName(Room start, String name) {
        // Простой обход — для учебного проекта с фиксированным миром
        Set<Room> visited = new HashSet<>();
        Queue<Room> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room r = queue.poll();
            if (name.equals(r.getName())) {
                return r;
            }
            for (Room neighbor : r.getNeighbors().values()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }
}
