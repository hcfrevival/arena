package net.hcfrevival.arena.player.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

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
}
