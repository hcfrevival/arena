package net.hcfrevival.arena.session.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.utils.Players;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.level.impl.TeamArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.hcfrevival.arena.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamSession implements ISession {
    @Getter public final ArenaPlugin plugin;
    @Getter public final UUID uniqueId;
    @Getter public final TeamArenaInstance arena;
    @Getter public final EGamerule gamerule;
    @Getter public final List<Team> teams;
    @Getter public final Set<ArenaPlayer> spectators;
    @Getter public final List<PlayerStatHolder> finalStats;
    @Getter @Setter public long startTimestamp;
    @Getter @Setter public long endTimestamp;
    @Getter @Setter public long expire;
    @Getter @Setter public boolean active;

    public TeamSession(ArenaPlugin plugin, EGamerule gamerule, TeamArenaInstance arena, List<Team> teams) {
        this.plugin = plugin;
        this.uniqueId = UUID.randomUUID();
        this.arena = arena;
        this.gamerule = gamerule;
        this.teams = teams;
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
        teams.forEach(t -> res.addAll(t.getFullMembers()));
        res.addAll(spectators);
        return res;
    }

    public boolean hasWinner() {
        return getWinner().isPresent();
    }

    public Optional<Team> getWinner() {
        final List<Team> aliveTeams = teams.stream().filter(t -> !t.getMembersByState(EPlayerState.INGAME).isEmpty()).collect(Collectors.toList());

        if (aliveTeams.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(aliveTeams.get(0));
    }

    public void sendMessage(String message) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                player.sendMessage(message);
            }
        });
    }

    public void sendSound(Sound sound) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                Players.playSound(player, sound);
            }
        });
    }

    @Override
    public void teleportAll() {
        int cursor = 0;

        for (Team team : teams) {
            if (arena.getSpawnpoints().size() < cursor) {
                cursor = 0;
            }

            final PLocatable spawnpoint = arena.getSpawnpoints().get(cursor);
            team.teleport(spawnpoint.getBukkitLocation());
            cursor += 1;
        }
    }
}
