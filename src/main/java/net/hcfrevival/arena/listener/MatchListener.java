package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.utils.Worlds;
import lombok.Getter;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

public record MatchListener(@Getter ArenaPlugin plugin) implements Listener {
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        final Location deathLocation = player.getLocation();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);

        event.setDeathMessage(null);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.getStatHolder().storeFinalAttributes(player));

        new Scheduler(plugin).sync(() -> {
            player.spigot().respawn();
            player.teleport(deathLocation);
            player.setVelocity(player.getVelocity().add(new Vector(0.0, 2.0, 0.0)));
            Worlds.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE);
        }).delay(1L).run();

        sessionManager.getSession(player).ifPresent(session -> {
            session.sendMessage(ArenaMessage.getArenaDeathMessage(player, killer));

            if (session instanceof final DuelSession duelSession) {
                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
                    arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD);
                });

                if (duelSession.hasWinner()) {
                    sessionManager.endSession(duelSession);
                }
            }

            else if (session instanceof final TeamSession teamSession) {
                playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
                    arenaPlayer.setCurrentState(EPlayerState.SPECTATE_DEAD);
                });

                if (teamSession.hasWinner()) {
                    sessionManager.endSession(teamSession);
                }
            }
        });
    }
}
