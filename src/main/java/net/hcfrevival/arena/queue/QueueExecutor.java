package net.hcfrevival.arena.queue;

import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.event.PlayerJoinQueueEvent;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.queue.impl.UnrankedQueueEntry;
import net.hcfrevival.arena.ranked.IRankedProfile;
import net.hcfrevival.arena.ranked.RankedManager;
import net.hcfrevival.arena.ranked.impl.RankedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public final class QueueExecutor {
    @Getter public final QueueManager manager;

    public void addToUnrankedQueue(Player player, EGamerule gamerule, Promise promise) {
        if (manager.getQueue(player).isPresent()) {
            promise.reject("You are already in queue. Please leave your current queue to join a different one.");
        }

        final UnrankedQueueEntry queueEntry = new UnrankedQueueEntry(player.getUniqueId(), gamerule);
        final PlayerJoinQueueEvent event = new PlayerJoinQueueEvent(player, queueEntry);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            promise.reject("Disallowed.");
            return;
        }

        manager.getQueueRepository().add(queueEntry);
    }

    public void addToRankedQueue(Player player, EGamerule gamerule, Promise promise) {
        RankedManager rankedManager = (RankedManager) manager.getPlugin().getManagers().get(RankedManager.class);

        if (manager.getQueue(player).isPresent()) {
            promise.reject("You are already in queue. Please leave your current queue to join a different one.");
            return;
        }

        if (!rankedManager.isProfileLoaded(player.getUniqueId())) {
            promise.reject("Your Ranked data is not loaded");
            return;
        }

        final Optional<IRankedProfile> profileQuery = rankedManager.getProfile(player.getUniqueId());
        if (profileQuery.isEmpty()) {
            promise.reject("Failed to load your Ranked data");
            return;
        }

        final RankedQueueEntry queueEntry = new RankedQueueEntry(player.getUniqueId(), gamerule, profileQuery.get().getRating(gamerule));
        final PlayerJoinQueueEvent event = new PlayerJoinQueueEvent(player, queueEntry);

        Bukkit.getPluginManager().callEvent(event);;

        if (event.isCancelled()) {
            promise.reject("Disallowed.");
            return;
        }

        manager.getQueueRepository().add(queueEntry);
    }
}
