package net.hcfrevival.arena.timer.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.timer.TimerManager;
import net.hcfrevival.arena.timer.impl.ArenaTimer;

@AllArgsConstructor
public class TimerUpdateTask implements Runnable {
    @Getter public TimerManager manager;

    @Override
    public void run() {
        final PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);

        playerManager.getPlayerRepository().stream().filter(ap -> !ap.getTimers().isEmpty()).forEach(arenaPlayer ->
                arenaPlayer.getTimers().stream().filter(ArenaTimer::isExpired).forEach(ArenaTimer::onFinish));
    }
}
