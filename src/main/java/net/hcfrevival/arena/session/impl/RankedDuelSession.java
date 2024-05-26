package net.hcfrevival.arena.session.impl;

import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;

public final class RankedDuelSession extends DuelSession {
    public RankedDuelSession(
            ArenaPlugin plugin,
            EGamerule gamerule,
            DuelArenaInstance arena,
            ArenaPlayer a,
            ArenaPlayer b
    ) {
        super(plugin, gamerule, arena, a, b);
    }
}
