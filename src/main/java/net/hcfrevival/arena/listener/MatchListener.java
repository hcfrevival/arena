package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.utils.Worlds;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.DuelMatchFinishEvent;
import net.hcfrevival.arena.event.TeamMatchFinishEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public record MatchListener(@Getter ArenaPlugin plugin) implements Listener {
    private void handleBlockInteraction(Player player, Block block, Cancellable cancellable) {
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)
                    || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE)
                    || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)
            ) {
                cancellable.setCancelled(true);
            }
        });
    }

    private void handleDeath(Player player, Player killer, boolean disconnect) {
        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        sessionManager.getSession(player).ifPresent(session -> {
            if (!disconnect) {
                session.sendMessage(ArenaMessage.getArenaDeathMessage(player, killer));
            }

            playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
                arenaPlayer.getStatHolder().storeFinalAttributes(player);
                session.saveStats(arenaPlayer);
            });

            if (session instanceof final DuelSession duelSession) {
                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD));

                if (duelSession.hasWinner() || disconnect) {
                    sessionManager.endSession(duelSession);
                }
            }

            else if (session instanceof final TeamSession teamSession) {
                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD));

                if (teamSession.hasWinner()) {
                    sessionManager.endSession(teamSession);
                }
            }
        });
    }

    @EventHandler
    public void onMatchFinish(DuelMatchFinishEvent event) {
        ArenaMessage.printMatchComplete(event.getSession());
    }

    @EventHandler
    public void onTeamMatchFinish(TeamMatchFinishEvent event) {
        ArenaMessage.printMatchComplete(event.getSession());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockInteraction(event.getPlayer(), event.getBlock(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBlockInteraction(event.getPlayer(), event.getBlock(), event);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Player killer = player.getKiller();

        handleDeath(player, killer, true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        final Location deathLocation = player.getLocation();

        event.setDeathMessage(null);

        new Scheduler(plugin).sync(() -> {
            player.spigot().respawn();
            player.teleport(deathLocation);
            player.setVelocity(player.getVelocity().add(new Vector(0.0, 2.0, 0.0)));
            Worlds.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE);
        }).delay(1L).run();

        handleDeath(player, killer, false);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }

        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        sessionManager.getSession(player).ifPresent(session -> {
            if (!session.isActive()) {
                event.setCancelled(true);
                return;
            }
        });

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)) {
                event.setCancelled(true);
                return;
            }
        });
    }
}
