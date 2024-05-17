package net.hcfrevival.arena.event;

import lombok.Getter;
import net.hcfrevival.arena.timer.impl.ArenaTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class ArenaTimerExpireEvent extends PlayerEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final ArenaTimer timer;

    public ArenaTimerExpireEvent(Player who, ArenaTimer arenaTimer) {
        super(who);
        this.timer = arenaTimer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
