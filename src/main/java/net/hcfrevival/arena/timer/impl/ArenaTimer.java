package net.hcfrevival.arena.timer.impl;

import gg.hcfactions.libs.base.timer.impl.GenericTimer;
import lombok.Getter;
import net.hcfrevival.arena.timer.ETimerType;

public final class ArenaTimer extends GenericTimer {
    @Getter public final ETimerType type;

    public ArenaTimer(ETimerType type, int seconds) {
        super(seconds);
        this.type = type;
    }
}
