package net.hcfrevival.arena.listener;

import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerDataListener(@Getter ArenaPlugin plugin) implements Listener {
    /**
     * Creates and stores ArenaPlayer object
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        playerManager.getPlayerRepository().add(new ArenaPlayer(player));
    }

    /**
     * Cleans up ArenaPlayer object
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        playerManager.getPlayerRepository().removeIf(ap -> ap.getUniqueId().equals(player.getUniqueId()));
    }
}
