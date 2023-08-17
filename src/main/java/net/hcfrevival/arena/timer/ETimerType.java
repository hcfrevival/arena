package net.hcfrevival.arena.timer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ETimerType {
    ENDERPEARL(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Enderpearl", 63),
    CRAPPLE(ChatColor.GOLD + "" + ChatColor.BOLD + "Crapple", 62);

    @Getter public final String displayName;
    @Getter public final int scoreboardPosition;
}
