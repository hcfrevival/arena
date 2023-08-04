package net.hcfrevival.arena.event;

import lombok.Getter;
import net.hcfrevival.arena.player.impl.EPlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerStateChangeEvent extends PlayerEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final EPlayerState previousState;
    @Getter public final EPlayerState newState;

    public PlayerStateChangeEvent(Player who, EPlayerState prev, EPlayerState next) {
        super(who);
        this.previousState = prev;
        this.newState = next;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
