package net.hcfrevival.arena;

import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaMessage {
    public static String getJoinQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked") + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName());
    }

    public static String getLeaveQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are no longer in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked" + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName()));
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
}
