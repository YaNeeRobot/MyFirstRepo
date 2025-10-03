package com.example.dungeon.core;

import com.example.dungeon.model.GameState;
import java.util.List;

@FunctionalInterface
public interface Command { void execute(GameState ctx, List<String> args); }
//ctx — это сокращение от context (англ. контекст). В программировании контекст — это объект,
//который содержит всю необходимую информацию о текущем состоянии среды, в которой выполняется
//какая-то операция.
