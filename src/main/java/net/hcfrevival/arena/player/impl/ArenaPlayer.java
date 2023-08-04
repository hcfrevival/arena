package net.hcfrevival.arena.player.impl;

import gg.hcfactions.libs.bukkit.scoreboard.AresScoreboard;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerStateChangeEvent;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class ArenaPlayer {
    @Getter public final ArenaPlugin plugin;
    @Getter public UUID uniqueId;
    @Getter public String username;
    @Getter EPlayerState currentState;
    @Getter @Setter public AresScoreboard scoreboard;
    @Getter @Setter public PlayerStatHolder statHolder;

    public ArenaPlayer(ArenaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.uniqueId = player.getUniqueId();
        this.username = player.getName();
        this.currentState = EPlayerState.LOBBY;
        this.scoreboard = new AresScoreboard(plugin, player, ChatColor.GOLD + "" + ChatColor.BOLD + "Arena");
        this.statHolder = null;
    }

    public boolean isInLobby() {
        return currentState.equals(EPlayerState.LOBBY) || currentState.equals(EPlayerState.LOBBY_IN_PARTY) || currentState.equals(EPlayerState.LOBBY_IN_QUEUE);
    }

    public boolean isAlive() {
        return currentState.equals(EPlayerState.INGAME);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uniqueId));
    }

    public void setCurrentState(EPlayerState state) {
        final PlayerStateChangeEvent changeEvent = new PlayerStateChangeEvent(getPlayer().get(), currentState, state);
        Bukkit.getPluginManager().callEvent(changeEvent);

        currentState = state;
    }
}
