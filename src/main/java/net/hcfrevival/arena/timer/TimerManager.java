package net.hcfrevival.arena.timer;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.timer.task.ScoreboardUpdateTask;
import net.hcfrevival.arena.timer.task.TimerUpdateTask;
import org.bukkit.scheduler.BukkitTask;

public final class TimerManager extends ArenaManager {
    @Getter public BukkitTask uiTask;
    @Getter public BukkitTask timerTask;

    public TimerManager(ArenaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        uiTask = new Scheduler(plugin).async(new ScoreboardUpdateTask(this)).repeat(0L, 1L).run();
        timerTask = new Scheduler(plugin).async(new TimerUpdateTask(this)).repeat(0L, 1L).run();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (uiTask != null) {
            uiTask.cancel();
            uiTask = null;
        }
    }
}
