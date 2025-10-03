package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();
    // в качестве значения Map-а принимает объект - блок кода - реализующий интерфейс Command, в его качестве используем лямбда-выражения

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> System.out.println("Команды: " + String.join(", ", commands.keySet())));
        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;  // три лонг переменные для проверки использования памяти
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });
        commands.put("look", (ctx, unused) -> System.out.println(ctx.getCurrent().describe())); //объектGameState.даетКомнату.даетОписаниеКомнаты
        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите какое-то направление: north, south, east, west");
            }
            String dir = a.getFirst().toLowerCase(Locale.ROOT); // переписываем направление в переменную
            Room current = ctx.getCurrent(); // объявил ссылку на поле Room current объекта-состояния среды ctx (контекст)
            Room next = current.getNeighbors().get(dir); // объявил ссылку на элемент Map-ы Map<String, Room> neighbors по ключу
            if (next == null) {
                throw new InvalidCommandException("Нельзя пойти на " + dir + " отсюда.");
            }
            ctx.setCurrent(next);       //моя реализация: здесь move делает current-комнатой next-комнату
            System.out.println("Вы перешли в: " + next.getName());
            System.out.println(next.describe());
        });

        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) {          //нужно указать после take название предмета
                throw new InvalidCommandException("Укажите название предмета");
            }
            String itemName = String.join(" ", a); // поддержка имён из нескольких слов
            Room room = ctx.getCurrent();           //взяли текущую комнату
            List<Item> items = room.getItems();     //вытаскиваем из нее список предметов
            Item target = null;                     //объявили переменную для искомого предмета
            for (Item item : items) {
                if (item.getName().equalsIgnoreCase(itemName)) {    //ищу в листе тот предмет
                    target = item;          //если он, то вносим его в переменную-объект
                    break;                  //и после гасим цикл
                }
            }
            if (target == null) {           //проверка
                throw new InvalidCommandException("В комнате нет предмета '" + itemName + "'");
            }
            items.remove(target);
            ctx.getPlayer().getInventory().add(target);
            System.out.println("Взято: " + target.getName());
        });

        commands.put("inventory", (ctx, a) -> {
            List<Item> inv = ctx.getPlayer().getInventory();
            if (inv.isEmpty()) {
                System.out.println("Инвентарь пуст.");
                return;
            }

            // Группировка по типу + сортировка по имени (Stream API)
            Map<String, List<Item>> grouped = inv.stream()
                    .collect(Collectors.groupingBy(
                            item -> item.getClass().getSimpleName(),
                            TreeMap::new, // сортировка по типу
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.stream()
                                            .sorted(Comparator.comparing(Item::getName))
                                            .toList()
                            )
                    ));

            System.out.println("Инвентарь:");
            grouped.forEach((type, items) -> {
                System.out.println("- " + type + " (" + items.size() + "): " +
                        items.stream().map(Item::getName).collect(Collectors.joining(", ")));
            });
        });

        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите название предмета для использования");
            }
            String itemName = String.join(" ", a);
            Player p = ctx.getPlayer();
            Item target = null;
            for (Item item : p.getInventory()) {
                if (item.getName().equalsIgnoreCase(itemName)) {
                    target = item;
                    break;
                }
            }
            if (target == null) {
                throw new InvalidCommandException("У вас нет предмета '" + itemName + "'");
            }
            target.apply(ctx); // полиморфизм через apply()
        });

        commands.put("fight", (ctx, a) -> {
            Room room = ctx.getCurrent();
            Monster monster = room.getMonster();
            if (monster == null) {
                throw new InvalidCommandException("В комнате нет монстра");
            }

            Player player = ctx.getPlayer();
            if (player.getHp() <= 0) {
                System.out.println("Вы мертвы. Нельзя сражаться.");
                return;
            }

            // Атака игрока
            int damageToMonster = player.getAttack();
            monster.setHp(monster.getHp() - damageToMonster);
            System.out.println("Вы бьёте " + monster.getName() + " на " + damageToMonster + ". HP монстра: " + Math.max(0, monster.getHp()));

            if (monster.getHp() <= 0) {
                System.out.println("Монстр побеждён! +50 очков.");
                ctx.addScore(50);
                // Дроп: добавим зелье как пример
                room.getItems().add(new Potion("Зелье победы", 10));
                room.setMonster(null);
                return;
            }

            // Атака монстра
            int monsterDamage = monster.getLevel() * 2 + 1; // простая формула
            player.setHp(player.getHp() - monsterDamage);
            System.out.println("Монстр отвечает на " + monsterDamage + ". Ваше HP: " + Math.max(0, player.getHp()));

            if (player.getHp() <= 0) {
                System.out.println("💀 Вы погибли! Игра окончена.");
                System.out.println("Ваш итоговый счёт: " + ctx.getScore());
                System.exit(0);
            }
        });
        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx));
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());
        commands.put("exit", (ctx, a) -> {
            System.out.println("Пока!");
            System.exit(0);
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room cave = new Room("Пещера", "Темно и сыро.");
        square.getNeighbors().put("север", forest);
        forest.getNeighbors().put("юг", square);
        forest.getNeighbors().put("восток", cave);
        cave.getNeighbors().put("запад", forest);

        forest.getItems().add(new Potion("Малое зелье", 5));
        forest.setMonster(new Monster("Волк", 1, 8));

        state.setCurrent(square);
    }

    public void run() {
        System.out.println("DungeonMini (TEMPLATE). 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) { //в этом цикле производится чтение команды из буфера, и потом ее выполнение
                System.out.print("> ");
                String line = in.readLine(); //читаем буфер
                if (line == null) break;
                line = line.trim();             // обрезает в начале и конце фразы пробелы(включая табы и переносы)
                if (line.isEmpty()) continue;   // если пусто, то итерация цикла пропускается
                List<String> parts = Arrays.asList(line.split("\\s+")); //делит строку на слова, \s+ - выражение «один или более пробельных символов»
                String cmd = parts.getFirst().toLowerCase(Locale.ROOT);   //Locale.ROOT однозначно приводит к нижнему регистру
                List<String> args = parts.subList(1, parts.size());     //не создает новый List, а обращается к части элементов, т е создает представление (view) списка parts
                Command c = commands.get(cmd); //получение обработчика команды из словаря по её имени
                //c — это ссылка (точнее, ссылочная переменная) на объект, реализующий интерфейс Command.
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1); //добавляет 1 очко за каждую успешно выполненную команду
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}
