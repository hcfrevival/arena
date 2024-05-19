package net.hcfrevival.arena.session.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.utils.Players;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DuelSession implements ISession {
    @Getter public final UUID uniqueId;
    @Getter public final DuelArenaInstance arena;
    @Getter public final EGamerule gamerule;
    @Getter public final ArenaPlayer playerA;
    @Getter public final ArenaPlayer playerB;
    @Getter public final Set<ArenaPlayer> spectators;
    @Getter public final List<PlayerStatHolder> finalStats;
    @Getter @Setter public boolean active;
    @Getter @Setter public long startTimestamp;
    @Getter @Setter public long endTimestamp;
    @Getter @Setter public long expire;

    public DuelSession(EGamerule gamerule, DuelArenaInstance arena, ArenaPlayer a, ArenaPlayer b) {
        this.uniqueId = UUID.randomUUID();
        this.arena = arena;
        this.gamerule = gamerule;
        this.playerA = a;
        this.playerB = b;
        this.spectators = Sets.newConcurrentHashSet();
        this.finalStats = Lists.newArrayList();
        this.startTimestamp = Time.now();
        this.endTimestamp = -1L;
        this.expire = -1L;
        this.active = false;
    }

    @Override
    public List<ArenaPlayer> getPlayers() {
        final List<ArenaPlayer> res = Lists.newArrayList();
        res.add(playerA);
        res.add(playerB);
        res.addAll(spectators);
        return res;
    }

    public boolean hasWinner() {
        return getWinner().isPresent();
    }

    public Optional<ArenaPlayer> getWinner() {
        if (!playerA.isAlive()) {
            return Optional.of(playerB);
        }

        if (!playerB.isAlive()) {
            return Optional.of(playerA);
        }

        return Optional.empty();
    }

    public Optional<ArenaPlayer> getLoser() {
        final Optional<ArenaPlayer> winnerQuery = getWinner();

        if (winnerQuery.isEmpty()) {
            return Optional.empty();
        }

        final ArenaPlayer winner = winnerQuery.get();
        final ArenaPlayer loser = (playerA.getUniqueId().equals(winner.getUniqueId()) ? playerB : playerA);
        return Optional.of(loser);
    }

    public Optional<PlayerStatHolder> getStats(ArenaPlayer player) {
        return finalStats.stream().filter(s -> s.getOwner().equals(player.getUniqueId())).findFirst();
    }

    @Override
    public void teleportAll() {
        final PLocatable spawnA = arena.getSpawnpoints().get(0);
        final PLocatable spawnB = arena.getSpawnpoints().get(1);

        playerA.getPlayer().ifPresent(a -> {
            a.teleport(spawnA.getBukkitLocation());
            Players.resetHealth(a);
        });

        playerB.getPlayer().ifPresent(b -> {
            b.teleport(spawnB.getBukkitLocation());
            Players.resetHealth(b);
        });
    }
}
