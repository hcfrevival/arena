package net.hcfrevival.arena.queue.impl;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.UUID;

public class RankedQueueEntry implements IArenaQueue {
    @Getter public final UUID uniqueId;
    @Getter public final EGamerule gamerule;
    @Getter public final int rating;
    @Getter @Setter public int range;

    public RankedQueueEntry(UUID uniqueId, EGamerule gamerule, int rating) {
        this.uniqueId = uniqueId;
        this.gamerule = gamerule;
        this.rating = rating;
        this.range = 50;
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
