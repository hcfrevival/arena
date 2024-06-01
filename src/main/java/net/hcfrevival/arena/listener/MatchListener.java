package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.utils.Worlds;
import lombok.Getter;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.DuelMatchFinishEvent;
import net.hcfrevival.arena.event.TeamMatchFinishEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

                try {
                    session.saveStats(arenaPlayer);
                } catch (NullPointerException e) {
                    player.sendMessage(Component.text("Failed to save your stats for this match", NamedTextColor.RED));
                }
            });

            if (session instanceof final DuelSession duelSession) {
                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD));

                if (duelSession.hasWinner() || disconnect) {
                    sessionManager.endSession(duelSession, false);
                }
            }

            else if (session instanceof final TeamSession teamSession) {
                if (disconnect) {
                    player.getInventory().forEach(item -> {
                        if (item != null) player.getWorld().dropItem(player.getLocation(), item);
                    });
                }

                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD));

                if (teamSession.hasWinner()) {
                    sessionManager.endSession(teamSession, false);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)
                    || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE)
                    || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)
            ) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        });
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
