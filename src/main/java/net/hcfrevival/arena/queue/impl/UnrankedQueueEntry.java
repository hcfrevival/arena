package net.hcfrevival.arena.queue.impl;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.UUID;

@Getter
public class UnrankedQueueEntry implements IArenaQueue {
    public UUID uniqueId;
    public EGamerule gamerule;
    @Setter public boolean locked;

    public UnrankedQueueEntry(UUID uniqueId, EGamerule gamerule) {
        this.uniqueId = uniqueId;
        this.gamerule = gamerule;
        this.locked = false;
    }
}
