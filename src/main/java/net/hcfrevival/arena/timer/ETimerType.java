package net.hcfrevival.arena.timer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ETimerType {
    ENDERPEARL(ChatColor.YELLOW + "" + ChatColor.BOLD + "Enderpearl", 12),
    CRAPPLE(ChatColor.GOLD + "" + ChatColor.BOLD + "Crapple", 13);

    @Getter public final String displayName;
    @Getter public final int scoreboardPosition;
}
