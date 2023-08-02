package net.hcfrevival.arena.player;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PlayerManager extends ArenaManager {
    @Getter public final Set<ArenaPlayer> playerRepository;

    public PlayerManager(ArenaPlugin plugin) {
        super(plugin);
        this.playerRepository = Sets.newConcurrentHashSet();
    }

    public Optional<ArenaPlayer> getPlayer(UUID uuid) {
        return playerRepository.stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst();
    }

    public Optional<ArenaPlayer> getPlayer(String username) {
        return playerRepository.stream().filter(player -> player.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public List<ArenaPlayer> getPlayersByState(EPlayerState state) {
        return playerRepository.stream().filter(player -> player.getCurrentState().equals(state)).collect(Collectors.toList());
    }
}
