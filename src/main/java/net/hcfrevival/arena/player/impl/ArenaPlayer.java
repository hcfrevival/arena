package net.hcfrevival.arena.player.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class ArenaPlayer {
    @Getter public UUID uniqueId;
    @Getter public String username;
    @Getter @Setter EPlayerState currentState;

    public ArenaPlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.username = player.getName();
        this.currentState = EPlayerState.LOBBY;
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
}
