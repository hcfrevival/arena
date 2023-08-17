package net.hcfrevival.arena.session.impl;

import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;

public final class RankedDuelSession extends DuelSession {
    public RankedDuelSession(EGamerule gamerule, DuelArenaInstance arena, ArenaPlayer a, ArenaPlayer b) {
        super(gamerule, arena, a, b);
    }
}
