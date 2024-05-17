package net.hcfrevival.arena.util;

import com.google.common.base.Strings;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scoreboard.AresScoreboard;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class ScoreboardUtil {
    /**
     * 63 --------------------
     * 62
     * 61
     * 60
     * 59
     * 58
     * 57
     * 56
     * 55
     * 54
     * 53
     * 52
     * 51
     * 50
     * 49
     * 48
     * 47
     * 46
     * 45
     * 44
     * 43
     * 42
     * 41
     * 40
     * 39
     * 38
     * 37
     * 36
     * 35
     * 34
     * 33
     * 32
     * 31
     * 30
     * 29
     * 28
     * 27
     * 26
     * 25
     * 24
     * 23
     * 22
     * 21
     * 20
     * 19
     * 18
     * 17
     * 16
     * 15
     * 14
     * 13 Enderpearl
     * 12 Crapple
     * 11 <blank space>
     * 10 <lobby reserved>
     * 9 <lobby reserved>
     * 8 <lobby reserved>
     * 7 <lobby reserved>
     * 6 <lobby reserved>
     * 5 <lobby reserved>
     * 4 <lobby reserved>
     * 3 <lobby reserved>
     * 2 <blank space>
     * 1 play.hcfrevival.net
     * 0 -------------------------
     */
    public static final int TIMER_SPACE_POS = 11;
    private static final String INDENT = ChatColor.RESET + " " + ChatColor.RESET + " ";

    private static void applyScoreboardTemplate(AresScoreboard scoreboard) {
        scoreboard.setLine(0, ChatColor.RESET + "" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", 24));
        scoreboard.setLine(2, ChatColor.RESET + " ");
        scoreboard.setLine(1, ChatColor.RED + "" + ChatColor.BOLD + "play.hcfrevival.net");
        scoreboard.setLine(63, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", 24));
    }

    public static void applyCooldownsToScoreboard(ArenaPlayer arenaPlayer) {
        if (!arenaPlayer.getTimers().isEmpty()) {
            arenaPlayer.getScoreboard().setLine(TIMER_SPACE_POS, ChatColor.RESET + " " + ChatColor.RESET + " " + ChatColor.RESET + " ");
        }

        arenaPlayer.getTimers().forEach(timer -> {
            final String time = (timer.getRemainingSeconds() < 10)
                    ? Time.convertToDecimal(timer.getRemaining()) + "s"
                    : Time.convertToHHMMSS(timer.getRemaining());

            arenaPlayer.getScoreboard().setLine(timer.getType().getScoreboardPosition(), timer.getType().getDisplayName() + ChatColor.RED + ": " + time);
        });
    }

    public static void sendLobbyScoreboard(ArenaPlugin plugin, Player player) {
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final QueueManager queueManager = (QueueManager) plugin.getManagers().get(QueueManager.class);
        final Optional<IArenaQueue> queue = queueManager.getQueue(player);
        final int onlineCount = Bukkit.getOnlinePlayers().size();

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            applyScoreboardTemplate(arenaPlayer.getScoreboard());
            arenaPlayer.getScoreboard().setLine(8, ChatColor.GOLD + "Online" + ChatColor.YELLOW + ": " + onlineCount);
            arenaPlayer.getScoreboard().setLine(7, ChatColor.GOLD + "In-Queue" + ChatColor.YELLOW + ": " + queueManager.getQueueRepository().size());

            queue.ifPresentOrElse(activeQueue -> {
                final boolean ranked = (activeQueue instanceof RankedQueueEntry);
                arenaPlayer.getScoreboard().setLine(6, ChatColor.RESET + "" + ChatColor.RESET + "");
                arenaPlayer.getScoreboard().setLine(5, ChatColor.GOLD + "Your Queue" + ChatColor.YELLOW + ":");
                arenaPlayer.getScoreboard().setLine(4, INDENT + activeQueue.getGamerule().getDisplayName() + " " + ChatColor.GRAY + (ranked ? "(Ranked)" : "(Unranked)"));

                if (ranked) {
                    final RankedQueueEntry rankedQueue = (RankedQueueEntry) activeQueue;
                    arenaPlayer.getScoreboard().setLine(3, INDENT + ChatColor.GOLD + "Search Range" + ChatColor.YELLOW + ": " + rankedQueue.getMinRating() + ChatColor.GRAY + "-" + ChatColor.YELLOW + rankedQueue.getMaxRating());
                }
            }, () -> {
                arenaPlayer.getScoreboard().removeLine(6);
                arenaPlayer.getScoreboard().removeLine(5);
                arenaPlayer.getScoreboard().removeLine(4);
                arenaPlayer.getScoreboard().removeLine(3);
            });

            if (player.getScoreboard() != arenaPlayer.getScoreboard().getInternal()) {
                player.setScoreboard(arenaPlayer.getScoreboard().getInternal());
            }
        });
    }

    public static void sendDuelScoreboard(ArenaPlugin plugin, Player player, DuelSession session) {
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final String sessionDuration = Time.convertToHHMMSS(session.getDuration());
        final int pingA;
        final int pingB;

        if (session.getPlayerA().getPlayer().isPresent()) {
            final Player playerA = session.getPlayerA().getPlayer().get();
            pingA = playerA.getPing();
        } else {
            pingA = 0;
        }

        if (session.getPlayerB().getPlayer().isPresent()) {
            final Player playerB = session.getPlayerB().getPlayer().get();
            pingB = playerB.getPing();
        } else {
            pingB = 0;
        }

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            applyScoreboardTemplate(arenaPlayer.getScoreboard());
            applyCooldownsToScoreboard(arenaPlayer);

            arenaPlayer.getScoreboard().setLine(4, ChatColor.GOLD + session.getPlayerA().getUsername() + ChatColor.YELLOW + ": " + pingA + "ms");
            arenaPlayer.getScoreboard().setLine(5, ChatColor.GOLD + session.getPlayerB().getUsername() + ChatColor.YELLOW + ": " + pingB + "ms");
            arenaPlayer.getScoreboard().setLine(6, ChatColor.RESET + "" + ChatColor.RESET);
            arenaPlayer.getScoreboard().setLine(7, ChatColor.GOLD + "Match Duration" + ChatColor.YELLOW + ": " + sessionDuration);
        });
    }

    public static void sendTeamScoreboard(ArenaPlugin plugin, Player player, TeamSession session) {

    }

    public static void sendSpectatorScoreboard(ArenaPlugin plugin, Player player, ISession session) {
        if (session instanceof final DuelSession duelSession) {

        }

        else if (session instanceof final TeamSession teamSession) {

        }
    }
}
