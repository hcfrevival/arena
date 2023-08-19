package net.hcfrevival.arena.player.impl;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.scoreboard.AresScoreboard;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerStateChangeEvent;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.hcfrevival.arena.timer.ETimerType;
import net.hcfrevival.arena.timer.impl.ArenaTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ArenaPlayer {
    @Getter public final ArenaPlugin plugin;
    @Getter public UUID uniqueId;
    @Getter public String username;
    @Getter EPlayerState currentState;
    @Getter @Setter public AresScoreboard scoreboard;
    @Getter @Setter public PlayerStatHolder statHolder;
    @Getter public final Set<ArenaTimer> timers;

    public ArenaPlayer(ArenaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.uniqueId = player.getUniqueId();
        this.username = player.getName();
        this.currentState = EPlayerState.LOBBY;
        this.scoreboard = new AresScoreboard(plugin, player, ChatColor.GOLD + "" + ChatColor.BOLD + "Arena");
        this.statHolder = null;
        this.timers = Sets.newConcurrentHashSet();
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
        getPlayer().ifPresent(bukkitPlayer -> {
            final PlayerStateChangeEvent changeEvent = new PlayerStateChangeEvent(bukkitPlayer, currentState, state);
            Bukkit.getPluginManager().callEvent(changeEvent);
        });

        currentState = state;
    }

    public Optional<ArenaTimer> getTimer(ETimerType type) {
        return timers.stream().filter(t -> t.getType().equals(type)).findFirst();
    }

    public boolean hasTimer(ETimerType type) {
        return timers.stream().anyMatch(t -> t.getType().equals(type));
    }

    public void addTimer(ArenaTimer timer) {
        timers.stream().filter(t -> t.getType().equals(timer.getType())).findFirst().ifPresentOrElse(existing ->
                existing.setExpire(timer.getExpire()), () -> timers.add(timer));
    }

    public void finishTimer(ArenaTimer timer) {
        if (timer.getType().equals(ETimerType.ENDERPEARL)) {

        }

        else if (timer.getType().equals(ETimerType.CRAPPLE)) {

        }

        timers.remove(timer);
    }
}
