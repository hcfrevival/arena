package net.hcfrevival.arena.timer.task;

import lombok.Getter;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.timer.TimerManager;
import net.hcfrevival.arena.util.ScoreboardUtil;

public record ScoreboardUpdateTask(@Getter TimerManager manager) implements Runnable {
    @Override
    public void run() {
        final PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        final SessionManager sessionManager = (SessionManager) manager.getPlugin().getManagers().get(SessionManager.class);

        playerManager.getPlayerRepository().forEach(arenaPlayer -> arenaPlayer.getPlayer().ifPresent(player -> {
            if (arenaPlayer.isInLobby()) {
                ScoreboardUtil.sendLobbyScoreboard(manager.getPlugin(), player);
            }

            else if (arenaPlayer.getCurrentState().equals(EPlayerState.INGAME) || arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)) {
                sessionManager.getSession(player).ifPresent(session -> {
                    if (session instanceof final DuelSession duelSession) {
                        ScoreboardUtil.sendDuelScoreboard(manager.getPlugin(), player, duelSession);
                    } else if (session instanceof final TeamSession teamSession) {
                        ScoreboardUtil.sendTeamScoreboard(manager.getPlugin(), player, teamSession);
                    }
                });
            } else if (arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE)) {
                sessionManager.getSession(player).ifPresent(session -> ScoreboardUtil.sendSpectatorScoreboard(manager.getPlugin(), player, session));
            }
        }));
    }
}
