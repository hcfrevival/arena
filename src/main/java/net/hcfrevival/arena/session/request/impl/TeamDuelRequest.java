package net.hcfrevival.arena.session.request.impl;

import gg.hcfactions.libs.base.util.Time;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.session.request.DuelRequestManager;
import net.hcfrevival.arena.session.IDuelRequest;
import net.hcfrevival.arena.team.impl.Team;

import java.util.UUID;

@Getter
public class TeamDuelRequest implements IDuelRequest<Team> {
    public final DuelRequestManager manager;
    public final UUID id;
    public final Team sender;
    public final Team receiver;
    public final EGamerule gamerule;
    public final long expire;

    public TeamDuelRequest(DuelRequestManager manager, Team sender, Team receiver, EGamerule gamerule) {
        this.manager = manager;
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.gamerule = gamerule;
        this.expire = Time.now() + (30 * 1000L);
    }
}
