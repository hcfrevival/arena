package net.hcfrevival.arena.level.impl;

import lombok.Getter;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.level.IArenaInstance;

import java.util.List;

public class DuelArena implements IArena {
    @Getter public final String name;
    @Getter public final String displayName;
    @Getter public final String authors;
    @Getter public final List<IArenaInstance> instances;

    public DuelArena(String name, String displayName, String authors, List<IArenaInstance> instances) {
        this.name = name;
        this.displayName = displayName;
        this.authors = authors;
        this.instances = instances;
    }
}
