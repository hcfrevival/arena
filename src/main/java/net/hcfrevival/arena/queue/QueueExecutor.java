package net.hcfrevival.arena.queue;

import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.event.PlayerJoinQueueEvent;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.queue.impl.UnrankedQueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        if (manager.getQueue(player).isPresent()) {
            promise.reject("You are already in queue. Please leave your current queue to join a different one.");
            return;
        }

        final RankedQueueEntry queueEntry = new RankedQueueEntry(player.getUniqueId(), gamerule, 1000); // TODO: Pull this rating from a query
        final PlayerJoinQueueEvent event = new PlayerJoinQueueEvent(player, queueEntry);

        Bukkit.getPluginManager().callEvent(event);;

        if (event.isCancelled()) {
            promise.reject("Disallowed.");
            return;
        }

        manager.getQueueRepository().add(queueEntry);
    }
}
