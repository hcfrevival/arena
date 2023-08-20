package net.hcfrevival.arena;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.util.Time;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.RankedDuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.team.impl.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class ArenaMessage {
    public static String getJoinQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked") + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName());
    }

    public static String getLeaveQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are no longer in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked" + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName()));
    }

    public static String getInQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are currently in queue for " + queue.getGamerule().getDisplayName() + " " + ChatColor.GRAY + (ranked ? "(Ranked)" : "(Unranked)");
    }

    public static String getArenaDetailMessage(IArena arena) {
        final boolean hasAuthor = (arena.getAuthors() != null && arena.getAuthors().length() > 0);
        return ChatColor.AQUA + "You are now playing " + ChatColor.AQUA + arena.getDisplayName() + (hasAuthor ? ChatColor.AQUA + " by " + ChatColor.LIGHT_PURPLE + arena.getAuthors() : "");
    }

    public static String getArenaDeathMessage(Player slain, Player killer) {
        if (killer != null) {
            return ChatColor.AQUA + slain.getName() + ChatColor.GRAY + " has been slain by " + ChatColor.AQUA + killer.getName();
        }

        return ChatColor.AQUA + slain.getName() + ChatColor.GRAY + " died";
    }

    public static String getKitAppliedMessage(String kitName) {
        return ChatColor.YELLOW + "Loaded Kit: " + ChatColor.BLUE + kitName;
    }

    public static String getItemLockedMessage(String itemName, long remainingDuration) {
         return ChatColor.RED + "Your " + ChatColor.RED + "" + ChatColor.BOLD + itemName + ChatColor.RED + " for " + Time.convertToDecimal(remainingDuration);
    }

    public static void printMatchComplete(ISession session) {
        session.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Match Complete!");

        if (session instanceof final DuelSession duelSession) {
            final ArenaPlayer winner = duelSession.getWinner().orElseThrow(NullPointerException::new);
            final ArenaPlayer loser = duelSession.getLoser().orElseThrow(NullPointerException::new);

            if (duelSession instanceof RankedDuelSession rankedDuelSession) {

                return;
            }

            session.sendMessage(ChatColor.GREEN + "Winner" + ChatColor.RESET + ": " + winner.getUsername());
            session.sendMessage(ChatColor.RED + "Loser" + ChatColor.RESET + ": " + loser.getUsername());
            session.sendMessage(
                    new ComponentBuilder("[View Inventories]")
                            .color(ChatColor.GRAY.asBungee())
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m inv " + session.getUniqueId().toString() + " " + winner.getUsername())).create());

            return;
        }

        if (session instanceof final TeamSession teamSession) {
            final Team winner = teamSession.getWinner().orElseThrow(NullPointerException::new);
            final List<Team> losers = teamSession.getTeams().stream().filter(t -> !t.getUniqueId().equals(winner.getUniqueId())).collect(Collectors.toList());
            final List<String> loserNames = Lists.newArrayList();

            losers.forEach(l -> loserNames.add(l.getDisplayName()));

            session.sendMessage(ChatColor.GREEN + "Winner" + ChatColor.RESET + ": " + winner.getDisplayName());
            session.sendMessage(ChatColor.RED + "Loser(s)" + ChatColor.RESET + ": " + Joiner.on(ChatColor.RESET + ", ").join(loserNames));

            return;
        }
    }
}
