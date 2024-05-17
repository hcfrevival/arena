package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.base.util.Strings;
import lombok.Getter;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.ArenaTimerExpireEvent;
import net.hcfrevival.arena.event.MatchFinishEvent;
import net.hcfrevival.arena.event.PlayerStateChangeEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.timer.ETimerType;
import net.hcfrevival.arena.timer.impl.ArenaTimer;
import net.hcfrevival.arena.util.ScoreboardUtil;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public record TimerListener(@Getter ArenaPlugin plugin) implements Listener {
    @EventHandler
    public void onPlayerEnderpearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof final Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            final ArenaTimer pearlTimer = arenaPlayer.getTimer(ETimerType.ENDERPEARL).orElse(null);

            if (pearlTimer != null) {
                player.sendMessage(ArenaMessage.getItemLockedMessage("Enderpearls", pearlTimer.getRemaining()));
                event.setCancelled(true);
                return;
            }

            arenaPlayer.addTimer(new ArenaTimer(arenaPlayer, ETimerType.ENDERPEARL, 16)); // TODO: Make configurable
        });
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        if (item.getType().equals(Material.GOLDEN_APPLE)) {
            playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
                final ArenaTimer crappleTimer = arenaPlayer.getTimer(ETimerType.CRAPPLE).orElse(null);

                if (crappleTimer != null) {
                    player.sendMessage(ArenaMessage.getItemLockedMessage("Crapples", crappleTimer.getRemaining()));
                    event.setCancelled(true);
                    return;
                }

                arenaPlayer.addTimer(new ArenaTimer(arenaPlayer, ETimerType.CRAPPLE, 45));
            });
        }
    }

    @EventHandler
    public void onTimerExpire(ArenaTimerExpireEvent event) {
        event.getTimer().getOwner().getScoreboard().removeLine(event.getTimer().getType().getScoreboardPosition());
        event.getPlayer().sendMessage(ArenaMessage.getItemUnlockedMessage(Strings.capitalize(event.getTimer().getType().name().toLowerCase().replaceAll("_", " "))));

        if (event.getTimer().getOwner().getTimers().isEmpty()) {
            event.getTimer().getOwner().getScoreboard().removeLine(ScoreboardUtil.TIMER_SPACE_POS);
        }
    }

    @EventHandler
    public void onMatchFinishFlushTimers(MatchFinishEvent event) {
        if (event.getSession() instanceof DuelSession duelSession) {
            duelSession.getPlayers().forEach(arenaPlayer -> arenaPlayer.getTimers().clear());
        }

        else if (event.getSession() instanceof TeamSession teamSession) {
            teamSession.getPlayers().forEach(arenaPlayer -> arenaPlayer.getTimers().clear());
        }
    }

    @EventHandler
    public void onPlayerStateChange(PlayerStateChangeEvent event) {
        final Player player = event.getPlayer();
        final EPlayerState oldState = event.getPreviousState();
        final EPlayerState newState = event.getNewState();
        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        if (!oldState.equals(newState)) {
            playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.getScoreboard().clear());
        }

        if (newState.equals(EPlayerState.LOBBY) || newState.equals(EPlayerState.LOBBY_IN_QUEUE) || newState.equals(EPlayerState.LOBBY_IN_PARTY)) {
            ScoreboardUtil.sendLobbyScoreboard(plugin, player);
        } else if (newState.equals(EPlayerState.INGAME) || newState.equals(EPlayerState.SPECTATE_DEAD)) {
            sessionManager.getSession(player).ifPresent(session -> {
                if (session instanceof final DuelSession duelSession) {
                    ScoreboardUtil.sendDuelScoreboard(plugin, player, duelSession);
                } else if (session instanceof final TeamSession teamSession) {
                    ScoreboardUtil.sendTeamScoreboard(plugin, player, teamSession);
                }
            });
        } else if (newState.equals(EPlayerState.SPECTATE)) {
            sessionManager.getSession(player).ifPresent(session -> ScoreboardUtil.sendSpectatorScoreboard(plugin, player, session));
        }
    }
}
