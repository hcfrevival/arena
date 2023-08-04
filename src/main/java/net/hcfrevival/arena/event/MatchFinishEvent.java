package net.hcfrevival.arena.event;

import lombok.Getter;
import net.hcfrevival.arena.session.ISession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchFinishEvent extends Event {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final ISession session;

    public MatchFinishEvent(ISession session) {
        this.session = session;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
