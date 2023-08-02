package net.hcfrevival.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class ArenaManager {
    @Getter public ArenaPlugin plugin;

    public void onEnable() {
        plugin.getAresLogger().info("Enabled Manager: " + getClass().getSimpleName());
    }

    public void onDisable() {
        plugin.getAresLogger().info("Disabled Manager: " + getClass().getSimpleName());
    }
}
