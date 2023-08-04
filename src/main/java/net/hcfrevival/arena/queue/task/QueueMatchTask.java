package net.hcfrevival.arena.queue.task;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.queue.impl.UnrankedQueueEntry;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.RankedDuelSession;

import java.util.List;
import java.util.Optional;

public record QueueMatchTask(@Getter QueueManager manager) implements Runnable {
    @Override
    public void run() {
        /*
            iterate over each queue, iterate again to see if there are any matches, once matched add to a list so it is exempt from future iterations
         */

        final PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        final SessionManager sessionManager = (SessionManager) manager.getPlugin().getManagers().get(SessionManager.class);
        final List<IArenaQueue> toRemove = Lists.newArrayList();

        for (IArenaQueue queueEntry : manager.getQueueRepository()) {
            if (queueEntry instanceof final RankedQueueEntry rankedQueueEntry) {
                final Optional<RankedQueueEntry> foundMatch = manager.getRankedQueues().stream().filter(otherQueue ->
                        !otherQueue.getUniqueId().equals(queueEntry.getUniqueId())
                                && otherQueue.getGamerule().equals(rankedQueueEntry.getGamerule())
                                && rankedQueueEntry.isMatch(otherQueue.getRating())
                                && otherQueue.isMatch(rankedQueueEntry.getRating())
                                && !toRemove.contains(otherQueue)).findAny();

                // No match, expand search
                if (foundMatch.isEmpty()) {
                    rankedQueueEntry.setRange(rankedQueueEntry.getRange() + 25);
                    continue;
                }

                final Optional<ArenaPlayer> playerQueryA = playerManager.getPlayer(queueEntry.getUniqueId());
                final Optional<ArenaPlayer> playerQueryB = playerManager.getPlayer(foundMatch.get().getUniqueId());

                if (playerQueryA.isEmpty() || playerQueryB.isEmpty()) {
                    manager.getPlugin().getAresLogger().error("failed to perform player query during queue processing");
                    return;
                }

                final Optional<RankedDuelSession> newSession = sessionManager.createRankedDuelSession(playerQueryA.get(), playerQueryB.get());

                if (newSession.isEmpty()) {
                    manager.getPlugin().getAresLogger().error("failed to generate duel session during queue processing");
                    continue;
                }

                new Scheduler(manager.getPlugin()).sync(() -> {
                    final DuelSession session = newSession.get();
                    sessionManager.startSession(session);
                }).run();

                toRemove.add(queueEntry);
                toRemove.add(foundMatch.get());

                continue;
            }

            if (queueEntry instanceof final UnrankedQueueEntry unrankedQueueEntry) {
                final Optional<UnrankedQueueEntry> foundMatch = manager.getUnrankedQueues().stream().filter(otherQueue ->
                        !otherQueue.getUniqueId().equals(unrankedQueueEntry.getUniqueId())
                                && otherQueue.getGamerule().equals(unrankedQueueEntry.getGamerule())
                                && !toRemove.contains(otherQueue)).findAny();

                if (foundMatch.isEmpty()) {
                    continue;
                }

                final Optional<ArenaPlayer> playerQueryA = playerManager.getPlayer(queueEntry.getUniqueId());
                final Optional<ArenaPlayer> playerQueryB = playerManager.getPlayer(foundMatch.get().getUniqueId());

                if (playerQueryA.isEmpty() || playerQueryB.isEmpty()) {
                    manager.getPlugin().getAresLogger().error("failed to perform player query during queue processing");
                    return;
                }

                toRemove.add(queueEntry);
                toRemove.add(foundMatch.get());

                final Optional<DuelSession> newSession = sessionManager.createDuelSession(playerQueryA.get(), playerQueryB.get(), false);

                if (newSession.isEmpty()) {
                    manager.getPlugin().getAresLogger().error("failed to generate duel session during queue processing");
                    continue;
                }

                new Scheduler(manager.getPlugin()).sync(() -> {
                    final DuelSession session = newSession.get();
                    sessionManager.startSession(session);
                }).run();
            }
        }

        toRemove.forEach(manager.getQueueRepository()::remove);
    }
}
