package net.hcfrevival.arena.listener;

import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerStateChangeEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.util.ScoreboardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public record TimerListener(@Getter ArenaPlugin plugin) implements Listener {
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
