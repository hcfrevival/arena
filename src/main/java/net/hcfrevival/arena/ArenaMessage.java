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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
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

    public static Component getArenaDetailMessage(IArena arena) {
        boolean hasAuthor = (arena.getAuthors() != null && !arena.getAuthors().isEmpty());
        Component component = Component.text("You are now playing", NamedTextColor.AQUA).appendSpace().append(arena.getDisplayName());

        if (hasAuthor) {
            component = component.appendSpace().append(Component.text("by", NamedTextColor.AQUA).appendSpace().append(Component.text(arena.getAuthors(), NamedTextColor.LIGHT_PURPLE)));
        }

        return component;
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
         return ChatColor.RED + "Your " + ChatColor.RED + "" + ChatColor.BOLD + itemName + ChatColor.RED + " are locked for " + ChatColor.RED + "" + ChatColor.BOLD + Time.convertToDecimal(remainingDuration) + ChatColor.RED + "s";
    }

    public static Component getItemUnlockedMessage(String itemName) {
        return Component.text("Your " + itemName + " has been unlocked", NamedTextColor.GREEN);
    }

    public static void printMatchComplete(ISession session) {
        session.sendMessage(Component.text("Match Complete!", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        if (session instanceof final DuelSession duelSession) {
            final ArenaPlayer winner = duelSession.getWinner().orElseThrow(NullPointerException::new);
            final ArenaPlayer loser = duelSession.getLoser().orElseThrow(NullPointerException::new);

            Component winnerComponent = Component.text("Winner", NamedTextColor.GREEN)
                    .append(Component.text(":", NamedTextColor.WHITE))
                    .appendSpace().append(Component.text(winner.getUsername(), NamedTextColor.WHITE)
                    .hoverEvent(Component.text("Click to view " + winner.getUsername() + "'s Inventory"))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/m inv " + session.getUniqueId() + " " + winner.getUsername())));

            Component loserComponent = Component.text("Loser", NamedTextColor.RED)
                    .append(Component.text(":", NamedTextColor.WHITE))
                    .appendSpace().append(Component.text(loser.getUsername(), NamedTextColor.WHITE)
                    .hoverEvent(Component.text("Click to view " + loser.getUsername() + "'s Inventory"))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/m inv " + session.getUniqueId() + " " + loser.getUsername())));

            session.sendMessage(winnerComponent.appendSpace().append(Component.text("-", NamedTextColor.GRAY)).appendSpace().append(loserComponent));

            return;
        }

        if (session instanceof final TeamSession teamSession) {
            final Team winner = teamSession.getWinner().orElseThrow(NullPointerException::new);
            final List<Team> losers = teamSession.getTeams().stream().filter(t -> !t.getUniqueId().equals(winner.getUniqueId())).toList();
            final List<String> winnerNames = Lists.newArrayList();
            final List<String> loserNames = Lists.newArrayList();

            winner.getFullMembers().forEach(wm -> winnerNames.add(wm.getUsername()));
            losers.forEach(l -> l.getFullMembers().forEach(lm -> loserNames.add(lm.getUsername())));

            Component winnerComponent = Component.text("Winner", NamedTextColor.GREEN).append(Component.text(":", NamedTextColor.WHITE)).appendSpace();
            for (int i = 0; i < winnerNames.size(); i++) {
                String winnerName = winnerNames.get(i);
                winnerComponent = winnerComponent.append(Component.text(winnerName, NamedTextColor.WHITE).clickEvent(ClickEvent.runCommand("/m inv " + session.getUniqueId() + " " + winnerName)).hoverEvent(Component.text("Click to view " + winnerName + "'s inventory")));

                if (i < (winnerNames.size() - 1)) {
                    winnerComponent = winnerComponent.append(Component.text(",")).appendSpace();
                }
            }

            Component loserComponent = Component.text("Loser", NamedTextColor.RED).append(Component.text(":", NamedTextColor.WHITE)).appendSpace();
            for (int i = 0; i < loserNames.size(); i++) {
                String loserName = loserNames.get(i);
                loserComponent = loserComponent.append(Component.text(loserName, NamedTextColor.WHITE).clickEvent(ClickEvent.runCommand("/m inv " + session.getUniqueId() + " " + loserName)).hoverEvent(Component.text("Click to view " + loserName + "'s inventory")));

                if (i < (loserNames.size() - 1)) {
                    loserComponent = loserComponent.append(Component.text(",")).appendSpace();
                }
            }

            session.sendMessage(winnerComponent);
            session.sendMessage(loserComponent);
        }
    }
}
