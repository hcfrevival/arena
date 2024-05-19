package net.hcfrevival.arena.ranked;

import gg.hcfactions.libs.base.connect.impl.mongo.MongoDocument;
import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.Map;
import java.util.UUID;

public interface IRankedProfile extends MongoDocument {
    UUID getUniqueId();
    Map<EGamerule, Integer> getRatings();

    default int getRating(EGamerule gamerule) {
        return getRatings().getOrDefault(gamerule, 1000);
    }

    default void setRating(EGamerule gamerule, int rating) {
        getRatings().put(gamerule, rating);
    }

    default int add(EGamerule gamerule, int amountToAdd) {
        int currentRating = getRating(gamerule);
        int newRating = currentRating + amountToAdd;

        setRating(gamerule, newRating);
        return newRating;
    }

    default int subtract(EGamerule gamerule, int amountToSubtract) {
        int currentRating = getRating(gamerule);
        int newRating = Math.max(0, (currentRating - amountToSubtract));

        setRating(gamerule, newRating);
        return newRating;
    }
}
