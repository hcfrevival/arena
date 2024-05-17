package net.hcfrevival.arena.team;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.impl.Team;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class TeamManager extends ArenaManager {
    @Getter public final TeamExecutor executor;
    @Getter public final Set<Team> teamRepository;

    public TeamManager(ArenaPlugin plugin) {
        super(plugin);
        this.executor = new TeamExecutor(this);
        this.teamRepository = Sets.newConcurrentHashSet();
    }

    public Optional<Team> getTeam(UUID uid) {
        return teamRepository.stream().filter(t -> t.getUniqueId().equals(uid)).findFirst();
    }

    public Optional<Team> getTeam(Player player) {
        return teamRepository.stream().filter(t -> t.isMember(player.getUniqueId())).findFirst();
    }

    public Optional<Team> getTeam(ArenaPlayer arenaPlayer) {
        return teamRepository.stream().filter(t -> t.isMember(arenaPlayer.getUniqueId())).findFirst();
    }

    public Optional<Team> getTeam(String teamName) {
        return teamRepository.stream().filter(t -> t.getLeader().getUsername().equalsIgnoreCase(teamName)).findFirst();
    }
}
