package net.hcfrevival.arena;

import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.queue.impl.RankedQueueEntry;
import org.bukkit.ChatColor;

public final class ArenaMessage {
    public static String getJoinQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked") + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName());
    }

    public static String getLeaveQueueMessage(IArenaQueue queue) {
        final boolean ranked = (queue instanceof RankedQueueEntry);
        return ChatColor.RESET + "You are no longer in queue for " + ChatColor.GOLD + (ranked ? "Ranked" : "Unranked" + " " + ChatColor.stripColor(queue.getGamerule().getDisplayName()));
    }
}
