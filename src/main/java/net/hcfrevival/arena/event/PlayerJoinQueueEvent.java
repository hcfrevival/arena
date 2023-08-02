package net.hcfrevival.arena.event;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerJoinQueueEvent extends PlayerEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();

    @Getter public final IArenaQueue queueEntry;
    @Getter @Setter public boolean cancelled;

    public PlayerJoinQueueEvent(Player who, IArenaQueue queueEntry) {
        super(who);
        this.queueEntry = queueEntry;
        this.cancelled = false;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
