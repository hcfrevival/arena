package net.hcfrevival.arena.player.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum EPlayerState {
    LOBBY(ChatColor.GREEN + "Lobby"),
    LOBBY_IN_QUEUE(ChatColor.GREEN + "Lobby " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(In Queue)"),
    LOBBY_IN_PARTY(ChatColor.GREEN + "Lobby " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(In Party)"),
    INGAME(ChatColor.RED + "In-game"),
    SPECTATE_DEAD(ChatColor.GRAY + "Spectating"),
    SPECTATE(ChatColor.GRAY + "Spectating");

    @Getter public String displayName;
}
