package net.hcfrevival.arena.session.request.impl;

import gg.hcfactions.libs.base.util.Time;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.request.DuelRequestManager;
import net.hcfrevival.arena.session.IDuelRequest;

import java.util.UUID;

@Getter
public class PlayerDuelRequest implements IDuelRequest<ArenaPlayer> {
    public final DuelRequestManager manager;
    public final UUID id;
    public final ArenaPlayer sender;
    public final ArenaPlayer receiver;
    public final EGamerule gamerule;
    public final long expire;

    public PlayerDuelRequest(DuelRequestManager manager, ArenaPlayer sender, ArenaPlayer receiver, EGamerule gamerule) {
        this.manager = manager;
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.gamerule = gamerule;
        this.expire = Time.now() + (30*1000L);
    }
}
