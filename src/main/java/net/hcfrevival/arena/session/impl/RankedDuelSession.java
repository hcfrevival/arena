package net.hcfrevival.arena.session.impl;

import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;

public final class RankedDuelSession extends DuelSession {
    public RankedDuelSession(DuelArenaInstance arena, ArenaPlayer a, ArenaPlayer b) {
        super(arena, a, b);
    }
}
