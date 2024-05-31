package net.hcfrevival.arena.queue.impl;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.UUID;

@Getter
public class RankedQueueEntry implements IArenaQueue {
    public final UUID uniqueId;
    public final EGamerule gamerule;
    public final int rating;
    @Setter public int range;
    @Setter public boolean locked;

    public RankedQueueEntry(UUID uniqueId, EGamerule gamerule, int rating) {
        this.uniqueId = uniqueId;
        this.gamerule = gamerule;
        this.rating = rating;
        this.range = 50;
        this.locked = false;
    }

    public int getMinRating() {
        return rating - range;
    }

    public int getMaxRating() {
        return rating + range;
    }

    public boolean isMatch(int otherRating) {
        return otherRating >= getMinRating() && otherRating <= getMaxRating();
    }
}
