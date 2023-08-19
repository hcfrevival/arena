package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.utils.Players;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerStateChangeEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        playerManager.getPlayerRepository().add(new ArenaPlayer(plugin, player));
    }

    /**
     * Cleans up ArenaPlayer object
     * @param event PlayerQuitEvent
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        playerManager.getPlayerRepository().removeIf(ap -> ap.getUniqueId().equals(player.getUniqueId()));
    }

    @EventHandler
    public void onPlayerStateChange(PlayerStateChangeEvent event) {
        final Player player = event.getPlayer();
        final EPlayerState newState = event.getNewState();

        if (newState.equals(EPlayerState.SPECTATE) || newState.equals(EPlayerState.SPECTATE_DEAD)) {
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if (newState.equals(EPlayerState.LOBBY)) {
            player.setGameMode(GameMode.SURVIVAL);
            LobbyUtil.giveLobbyItems(plugin, player);
            return;
        }

        if (newState.equals(EPlayerState.INGAME)) {
            Players.resetHealth(player);
        }
    }
}
