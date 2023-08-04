package net.hcfrevival.arena.listener;

import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public record LobbyListener(@Getter ArenaPlugin plugin) implements Listener {
    private void handleGenericLobbyEvent(Player player, Cancellable cancellable, boolean canBypass) {
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final Optional<ArenaPlayer> arenaPlayerQuery = playerManager.getPlayer(player.getUniqueId());

        if (arenaPlayerQuery.isEmpty()) {
            plugin.getAresLogger().error("Failed to perform Arena Player Query in InventoryClickEvent");
            cancellable.setCancelled(true);
            return;
        }

        final ArenaPlayer ap = arenaPlayerQuery.get();

        if (ap.isInLobby() && !canBypass) {
            cancellable.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        LobbyUtil.giveLobbyItems(plugin, player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) {
            return;
        }

        handleGenericLobbyEvent(player, event, true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        handleGenericLobbyEvent(player, event, false);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }

        handleGenericLobbyEvent(player, event, false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        handleGenericLobbyEvent(player, event, true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        handleGenericLobbyEvent(player, event, true);
    }
}
