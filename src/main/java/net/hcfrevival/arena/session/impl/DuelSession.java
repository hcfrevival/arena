package net.hcfrevival.arena.session.impl;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.utils.Players;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.ISession;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DuelSession implements ISession {
    @Getter public final UUID uniqueId;
    @Getter public final DuelArenaInstance arena;
    @Getter public final ArenaPlayer playerA;
    @Getter public final ArenaPlayer playerB;
    @Getter public final Set<ArenaPlayer> spectators;
    @Getter @Setter public long startTimestamp;
    @Getter @Setter public long endTimestamp;

    public DuelSession(DuelArenaInstance arena, ArenaPlayer a, ArenaPlayer b) {
        this.uniqueId = UUID.randomUUID();
        this.arena = arena;
        this.playerA = a;
        this.playerB = b;
        this.spectators = Sets.newConcurrentHashSet();
        this.startTimestamp = Time.now();
        this.endTimestamp = -1L;
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
