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
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;  // Три long переменные для проверки использования памяти
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });

        commands.put("alloc", (ctx, a) -> {     //вместо gc-stats умещаю замеры памяти до и после alloc
            Runtime rt = Runtime.getRuntime();

            // Состояние ДО
            long freeBefore = rt.freeMemory(), totalBefore = rt.totalMemory(), usedBefore = totalBefore - freeBefore;
            System.out.println("Память ДО: used=" + usedBefore + " free=" + freeBefore + " total=" + totalBefore);

            // Выделяем 50 МБ
            System.out.println("Выделяем 50 МБ памяти...");
            List<byte[]> garbage = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                garbage.add(new byte[1_000_000]);
            }

            // Состояние ПОСЛЕ выделения (ДО GC)
            long freeAfterAlloc = rt.freeMemory(), totalAfterAlloc = rt.totalMemory(), usedAfterAlloc = totalAfterAlloc - freeAfterAlloc;
            System.out.println("Память ПОСЛЕ выделения: used=" + usedAfterAlloc + " free=" + freeAfterAlloc + " total=" + totalAfterAlloc);

            // Запускаем GC
            System.out.println("Вызываем сборку мусора (GC)...");
            System.gc();

            // Состояние ПОСЛЕ GC
            long freeAfterGC = rt.freeMemory(), totalAfterGC = rt.totalMemory(), usedAfterGC = totalAfterGC - freeAfterGC;
            System.out.println("Память ПОСЛЕ GC: used=" + usedAfterGC + " free=" + freeAfterGC + " total=" + totalAfterGC);
        });

        commands.put("look", (ctx, unused) -> System.out.println(ctx.getCurrent().describe())); //объектGameState.даетКомнату.даетОписаниеКомнаты
        commands.put("stats", (ctx, a) -> {
            Player p = ctx.getPlayer();
            System.out.println("Здоровье: " + p.getHp());
            System.out.println("Атака: " + p.getAttack());
        });
        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите какое-то направление: север, юг, восток, запад");
            }
            String dir = a.getFirst().toLowerCase(Locale.ROOT); // переписываем направление в переменную
            Room current = ctx.getCurrent(); // скопировал ссылку на поле Room current объекта-состояния среды ctx (контекст)

            // 🔑 Проверяем, не закрыта ли дверь в этом направлении
            if (current.isExitLocked(dir)) {
                // Ищем в инвентаре любой предмет типа Key (ключ)
                boolean hasKey = ctx.getPlayer().getInventory().stream()
                        .anyMatch(item -> item instanceof Key);
                if (!hasKey) {
                    throw new InvalidCommandException("Дверь на " + dir + " заперта! Нужен ключ.");
                }
                // Открываем дверь (удаляем блокировку)
                current.unlockExit(dir);
                System.out.println("Вы использовали ключ и открыли дверь на " + dir + "!");
            }

            Room next = current.getNeighbors().get(dir); // объявил ссылку на элемент Map-ы Map<String, Room> neighbors по ключу
            if (next == null) {
                throw new InvalidCommandException("Нельзя пойти на " + dir + " отсюда.");
            }
            ctx.setCurrent(next); // моя реализация: здесь move делает current-комнатой next-комнату
            System.out.println("Вы перешли в: " + next.getName());
            System.out.println(next.describe());
        });

        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) {          //нужно указать после take название предмета
                throw new InvalidCommandException("Укажите название предмета");
            }
            String itemName = String.join(" ", a); // поддержка имён из нескольких слов
            Room room = ctx.getCurrent();           //копирую ссылку на текущую комнату
            List<Item> items = room.getItems();     //копирую ссылку на ее items-сы(предметы)
            Item target = null;                     //объявил переменную для искомого предмета
            for (Item item : items) {
                if (item.getName().equalsIgnoreCase(itemName)) {    //ищу в листе тот предмет
                    target = item;          //если он, то вносим его в переменную-объект
                    break;                  //и после гасим цикл
                }
            }
            if (target == null) {           //проверка
                throw new InvalidCommandException("В комнате нет предмета '" + itemName + "'");
            }
            items.remove(target);           //из items-сов комнаты удаляю предмет
            ctx.getPlayer().getInventory().add(target);     //и добавляю предмет в инвентарь перса
            System.out.println("Взято: " + target.getName());
        });

        commands.put("inventory", (ctx, a) -> {
            List<Item> inv = ctx.getPlayer().getInventory();   //ссылка на List(инвентарь) перса
            if (inv.isEmpty()) {
                System.out.println("Инвентарь пустой.");
                return;
            }

            // Группировка по типу + сортировка по имени (Stream API)
            // т е команда выводит содержание инвентаря, отсортированное сначала по типу потом по алфавиту внутри группы типа
            Map<String, List<Item>> grouped = inv.stream()      //запускаю стрим, результат -> grouped
                    //«Взять список → сгруппировать по типу → отсортировать внутри групп → вернуть карту».
                    .collect(Collectors.groupingBy(     //терминальный метод, обрабатывает стрим и возвращает результат
                            item -> item.getClass().getSimpleName(),  // Ключ группировки. Эта лямбда, достает имя типа предметов
                            TreeMap::new,                                  // такая Map-а сортирует по ключу
                            Collectors.collectingAndThen(                  // Постобработка значений
                                    Collectors.toList(),                   // Сначала собирает все предметы одного типа в обычный List
                                    list -> list.stream()        // Затем применяет функцию к этому списку: создаёт из него stream,
                                            .sorted(Comparator.comparing(Item::getName))    //сортирует по item.getName() (алфавитно по названию),
                                            .toList()                      // собирает обратно в новый неизменяемый список.
                            )
                    ));

            System.out.println("Инвентарь:");
            grouped.forEach((type, items) -> {
                System.out.println("- " + type + " (" + items.size() + "): " +
                        items.stream().map(Item::getName).collect(Collectors.joining(", ")));
            });         // тут стрим выводит отсортированный инвентарь grouped
        });

        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите название предмета для использования");
            }
            String itemName = String.join(" ", a);      //соединяет слова в одно название
            Player p = ctx.getPlayer();                         //ссылка на перса
            Item target = null;                                 //ссылочная переменная для используемого предмета
            for (Item item : p.getInventory()) {                //ищем предмет в инвентаре перса
                if (item.getName().equalsIgnoreCase(itemName)) {
                    target = item;                              //находим ссылку на него
                    break;
                }
            }
            if (target == null) {
                throw new InvalidCommandException("У вас нет предмета '" + itemName + "'");
            }
            target.apply(ctx); // "Применяем". Здесь собственно полиморфизм через apply()
        });

        commands.put("fight", (ctx, a) -> {
            Room room = ctx.getCurrent();
            List<Monster> monsters = room.getMonsters();

            if (monsters.isEmpty()) {
                throw new InvalidCommandException("В комнате нет монстров");
            }

            Player player = ctx.getPlayer();
            if (player.getHp() <= 0) {
                System.out.println("Вы мертвы. Нельзя сражаться.");
                return;
            }

            // Если монстр один — бьём его без вопросов
            Monster target;
            if (monsters.size() == 1) {
                target = monsters.get(0);
            } else {
                // Если монстров несколько — нужно выбрать по имени
                if (a.isEmpty()) {
                    System.out.println("В комнате несколько монстров:");
                    for (int i = 0; i < monsters.size(); i++) {
                        Monster m = monsters.get(i);
                        System.out.println((i + 1) + ". " + m.getName() + " (HP: " + m.getHp() + ")");
                    }
                    throw new InvalidCommandException("Укажите имя монстра: fight <имя>");
                }
                String monsterName = String.join(" ", a);
                target = monsters.stream()
                        .filter(m -> m.getName().equalsIgnoreCase(monsterName))
                        .findFirst()
                        .orElseThrow(() -> new InvalidCommandException("Монстр '" + monsterName + "' не найден"));
            }

            // Атака игрока
            int baseDamage = player.getAttack();
            int damageToMonster = baseDamage;

            // Особое условие: Безымянный Страж уязвим ТОЛЬКО к Мечу Хранителей
            if ("Безымянный Страж".equals(target.getName())) {
                boolean hasSword = player.getInventory().stream()
                        .anyMatch(item -> "Меч Хранителей".equals(item.getName()));
                if (!hasSword) {
                    System.out.println("Безымянный Страж неуязвим! Вам нужен Меч Хранителей!");
                    damageToMonster = 0;
                }
            }
            target.setHp(target.getHp() - damageToMonster);
            System.out.println("Вы бьёте " + target.getName() + " на " + damageToMonster + ". HP: " + Math.max(0, target.getHp()));

            if (target.getHp() <= 0) {
                System.out.println("Монстр " + target.getName() + " побеждён! +50 очков.");
                ctx.addScore(50);
                // Дроп: например, зелье
                room.getItems().add(new Potion("Зелье победы", 10));
                monsters.remove(target); // удаляем из комнаты
                // Проверка на финального босса
                if ("Безымянный Страж".equals(target.getName())) {
                    System.out.println("Артефакт взрывается ослепительным светом! Проклятие снято.");
                    System.out.println("🎉 ПОБЕДА! Ты спас Эльдарию!");
                    SaveLoad.writeScore(ctx.getPlayer().getName(), ctx.getScore());
                    System.exit(0);
                }
                return;
            }

            // Атака монстра
            int monsterDamage = target.getLevel() * 2 + 1;
            player.setHp(player.getHp() - monsterDamage);
            System.out.println(target.getName() + " атакует на " + monsterDamage + ". Ваше HP: " + Math.max(0, player.getHp()));

            if (player.getHp() <= 0) {
                System.out.println("💀 Вы погибли! Игра окончена.");
                System.out.println("Ваш итоговый счёт: " + ctx.getScore());
                SaveLoad.writeScore(ctx.getPlayer().getName(), ctx.getScore());
                System.out.println("Игра окончена. Спасибо за игру!");
                return; // выйти из команды, а не из JVM
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

        Room square = new Room("Площадь", "Каменная площадь с целебным фонтаном. Последний луч света перед тьмой.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет. Но ветер несёт запах тлена...");
        Room cave = new Room("Пещера", "Темно и сыро. Где-то слышен звон цепей...");
        Room secret = new Room("Тайная комната", "Здесь пахнет древностью. На камне лежит Меч Хранителей — последняя надежда против тьмы.");
        Room sanctuary = new Room("Святилище Артефакта", "Тьма сгущается. На пьедестале пульсирует Чёрный Артефакт.");

        // Связи между комнатами
        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);
        cave.getNeighbors().put("north", secret);
        secret.getNeighbors().put("south", cave);
        secret.getNeighbors().put("east", sanctuary); // ← дверь в святилище
        sanctuary.getNeighbors().put("west", secret);

        // Закрываем дверь в Тайную комнату и в Святилище
        cave.lockExit("north");        // дверь в Тайную комнату
        secret.lockExit("east");       // дверь в Святилище

        // Предметы
        forest.getItems().add(new Potion("Малое зелье", 5));
        cave.getItems().add(new Key("Чёрный Ключ"));
        secret.getItems().add(new Weapon("Меч Хранителей", 10));

        // Монстры
        forest.getMonsters().add(new Monster("Волк", 1, 8));
        cave.getMonsters().add(new Monster("Гоблин-страж", 2, 15));
        cave.getMonsters().add(new Monster("Скелет", 1, 10)); // второй монстр
        sanctuary.getMonsters().add(new Monster("Безымянный Страж", 5, 100));

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
        } finally {
            // Сохраняем счёт при любом выходе из игры
            if (state.getPlayer() != null) {
                SaveLoad.writeScore(state.getPlayer().getName(), state.getScore());
            }
        }
    }
}
