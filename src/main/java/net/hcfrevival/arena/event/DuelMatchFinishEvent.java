package net.hcfrevival.arena.event;

import lombok.Getter;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.impl.DuelSession;

public final class DuelMatchFinishEvent extends MatchFinishEvent {
    @Getter public final ArenaPlayer winner;
    @Getter public final ArenaPlayer loser;

    public DuelMatchFinishEvent(DuelSession session, ArenaPlayer winner, ArenaPlayer loser) {
        super(session);
        this.winner = winner;
        this.loser = loser;
    }
}
