package net.hcfrevival.arena.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.queue.impl.UnrankedQueueEntry;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class QueueManager extends ArenaManager {
    @Getter public Set<IArenaQueue> queueRepository;
    @Getter public QueueExecutor executor;

    public QueueManager(ArenaPlugin plugin) {
        super(plugin);
        this.queueRepository = Sets.newConcurrentHashSet();
        this.executor = new QueueExecutor(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        queueRepository.clear();
    }

    public Optional<IArenaQueue> getQueue(Player player) {
        return queueRepository.stream().filter(queue -> queue.getUniqueId().equals(player.getUniqueId())).findFirst();
    }

    public List<RankedQueueEntry> getRankedQueues() {
        final List<RankedQueueEntry> res = Lists.newArrayList();
        queueRepository.stream().filter(queue -> queue instanceof RankedQueueEntry).forEach(rankedQueue -> res.add((RankedQueueEntry) rankedQueue));
        return res;
    }

    public List<UnrankedQueueEntry> getUnrankedQueues() {
        final List<UnrankedQueueEntry> res = Lists.newArrayList();
        queueRepository.stream().filter(queue -> queue instanceof UnrankedQueueEntry).forEach(unrankedQueue -> res.add((UnrankedQueueEntry) unrankedQueue));
        return res;
    }
}
