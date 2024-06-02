package net.hcfrevival.arena.listener;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.events.impl.PlayerBigMoveEvent;
import gg.hcfactions.libs.bukkit.events.impl.PlayerDamagePlayerEvent;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.IArenaInstance;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public final class SpectatorListener implements Listener {
    @Getter ArenaPlugin plugin;
    private Set<UUID> recentlyDied;

    public SpectatorListener(ArenaPlugin plugin) {
        this.plugin = plugin;
        this.recentlyDied = Sets.newConcurrentHashSet();
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        ((PlayerManager)plugin.getManagers().get(PlayerManager.class)).getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE) || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)) {
                ((SessionManager)plugin.getManagers().get(SessionManager.class)).getSession(player).ifPresent(session -> session.stopSpectating(arenaPlayer));
            }
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID uuid = player.getUniqueId();

        recentlyDied.add(player.getUniqueId());
        new Scheduler(plugin).sync(() -> recentlyDied.remove(uuid)).delay(100L).run();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerBigMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);

        sessionManager.getSession(player).ifPresent(session -> {
            final IArenaInstance arenaInstance = session.getArena();

            if (!recentlyDied.contains(player.getUniqueId()) && !arenaInstance.getRegion().isInside(new PLocatable(player), false)) {
                event.setCancelled(true);

                new Scheduler(plugin).sync(() -> {
                    player.teleport(arenaInstance.getSpectatorSpawnpoint().getBukkitLocation());
                    player.sendMessage(ChatColor.RED + "You have been teleported back to the Spectator Spawnpoint");
                }).delay(1L).run();
            }
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player attacker = event.getDamager();
        Player attacked = event.getDamaged();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(attacker.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE) || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)) {
                attacker.setSpectatorTarget(attacked);
                event.setCancelled(true);
            }
        });
    }
}
