package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public record LobbyListener(@Getter ArenaPlugin plugin) implements Listener {
    private void handleGenericLobbyEvent(Player player, Cancellable cancellable, boolean canBypass) {
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final Optional<ArenaPlayer> arenaPlayerQuery = playerManager.getPlayer(player.getUniqueId());

        if (arenaPlayerQuery.isEmpty()) {
            plugin.getAresLogger().error("Failed to perform Arena Player Query");
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
        new Scheduler(plugin).sync(() -> player.teleport(plugin.getConfiguration().getSpawnLocation().getBukkitLocation())).delay(1L).run();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) {
            return;
        }

        handleGenericLobbyEvent(player, event, true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        if (player.hasPermission(APermissions.A_ADMIN)) {
            return;
        }

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)) {
                return;
            }

            event.setCancelled(true);
            event.getItemDrop().remove();
        });
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
        handleGenericLobbyEvent(player, event, player.hasPermission(APermissions.A_ADMIN));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        handleGenericLobbyEvent(player, event, player.hasPermission(APermissions.A_ADMIN));
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            handleGenericLobbyEvent(event.getPlayer(), event, player.hasPermission(APermissions.A_ADMIN));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof final Player player) {
            PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

            playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
                if (arenaPlayer.isInLobby()) {
                    event.setFoodLevel(20);
                    event.setCancelled(true);
                }
            });
        }
    }
}
