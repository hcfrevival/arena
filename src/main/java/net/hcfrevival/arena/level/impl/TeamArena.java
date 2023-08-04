package net.hcfrevival.arena.level.impl;

import net.hcfrevival.arena.level.IArenaInstance;

import java.util.List;

public class TeamArena extends DuelArena {
    public TeamArena(String name, String displayName, String authors, List<IArenaInstance> instances) {
        super(name, displayName, authors, instances);
    }
}
