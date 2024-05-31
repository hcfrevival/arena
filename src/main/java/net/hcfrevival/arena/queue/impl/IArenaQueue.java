package net.hcfrevival.arena.queue.impl;

import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.UUID;

public interface IArenaQueue {
    UUID getUniqueId();
    EGamerule getGamerule();
    boolean isLocked();
    void setLocked(boolean b);
}
