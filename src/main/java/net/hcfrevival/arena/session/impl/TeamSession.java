package net.hcfrevival.arena.session.impl;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.level.impl.TeamArenaInstance;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.team.Team;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamSession implements ISession {
    @Getter public final UUID uniqueId;
    @Getter public final TeamArenaInstance arena;
    @Getter public final List<Team> teams;
    @Getter public Set<UUID> spectators;
    @Getter @Setter public long startTimestamp;
    @Getter @Setter public long endTimestamp;

    public TeamSession(TeamArenaInstance arena, List<Team> teams) {
        this.uniqueId = UUID.randomUUID();
        this.arena = arena;
        this.teams = teams;
        this.spectators = Sets.newConcurrentHashSet();
        this.startTimestamp = Time.now();
        this.endTimestamp = -1L;
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
}
