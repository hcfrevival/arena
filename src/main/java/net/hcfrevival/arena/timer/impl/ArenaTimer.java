package net.hcfrevival.arena.timer.impl;

import gg.hcfactions.libs.base.timer.impl.GenericTimer;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.event.ArenaTimerExpireEvent;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.timer.ETimerType;
import org.bukkit.Bukkit;

@Getter
public final class ArenaTimer extends GenericTimer {
    public ArenaPlayer owner;
    public final ETimerType type;

    public ArenaTimer(ArenaPlayer owner, ETimerType type, int seconds) {
        super(seconds);
        this.owner = owner;
        this.type = type;
    }

    public void onFinish() {
        owner.getTimers().remove(this);

        new Scheduler(getOwner().getPlugin()).sync(() ->
                owner.getPlayer().ifPresent(bukkitPlayer -> Bukkit.getPluginManager().callEvent(new ArenaTimerExpireEvent(bukkitPlayer, this)))).run();
    }
}
