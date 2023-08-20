package net.hcfrevival.arena.event;

import lombok.Getter;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.team.impl.Team;

import java.util.List;

public final class TeamMatchFinishEvent extends MatchFinishEvent {
    @Getter public final Team winner;
    @Getter public final List<Team> losers;

    public TeamMatchFinishEvent(ISession session, Team winner, List<Team> losers) {
        super(session);
        this.winner = winner;
        this.losers = losers;
    }
}
