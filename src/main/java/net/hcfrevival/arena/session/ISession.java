package net.hcfrevival.arena.session;

import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.utils.Players;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.level.IArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ISession {
    /**
     * @return Session ID
     */
    UUID getUniqueId();

    /**
     * @return Arena Instance
     */
    IArenaInstance getArena();

    /**
     * @return Session Rule set
     */
    EGamerule getGamerule();

    /**
     * @return List of Spectators
     */
    Set<ArenaPlayer> getSpectators();

    /**
     * @return Match start time
     */
    long getStartTimestamp();

    /**
     * @return Match end time (defaults to -1L if not ended)
     */
    long getEndTimestamp();

    /**
     * @return Time until this session will be expired from match history cache
     */
    long getExpire();

    /**
     * @return Returns true if this event is active and players can be attacked
     */
    boolean isActive();

    /**
     * Update the session start time
     * @param l Epoch millis
     */
    void setStartTimestamp(long l);

    /**
     * Update the session end time
     * @param l Epoch millis
     */
    void setEndTimestamp(long l);

    /**
     * Update the session expire time in match history
     * @param l Epoch millis
     */
    void setExpire(long l);

    /**
     * @param b Sets the active state for this session
     */
    void setActive(boolean b);

    /**
     * Collects and returns all ArenaPlayer instances associated with this session
     * @return List of ArenaPlayer
     */
    List<ArenaPlayer> getPlayers();

    /**
     * @return Collection of PlayerStatHolders
     */
    List<PlayerStatHolder> getFinalStats();

    /**
     * Return the time in millis this session
     * has lasted.
     * @return Epoch millis
     */
    default long getDuration() {
        if (getEndTimestamp() == -1L) {
            return Time.now() - getStartTimestamp();
        }

        return getEndTimestamp() - getStartTimestamp();
    }

    /**
     * @param uniqueId Bukkit UUID
     * @return Returns true if the provided UUID is spectating this session
     */
    default boolean isSpectating(UUID uniqueId) {
        return getSpectators().stream().anyMatch(spec -> spec.getUniqueId().equals(uniqueId));
    }

    /**
     * Teleport all players to the arena
     */
    void teleportAll();

    /**
     * Moves an ArenaPlayer to an Arena and marks them as a spectator
     * @param player ArenaPlayer
     */
    default void startSpectating(ArenaPlayer player) {
        if (getSpectators().contains(player)) {
            return;
        }

        player.getPlayer().ifPresent(bukkitPlayer -> {
            player.setCurrentState(EPlayerState.SPECTATE);

            bukkitPlayer.teleport(getArena().getSpectatorSpawnpoint().getBukkitLocation());
            bukkitPlayer.setGameMode(GameMode.SPECTATOR);
            Players.resetHealth(bukkitPlayer);

            getSpectators().add(player);
        });
    }

    /**
     * Moves an ArenaPlayer out of an Arena and marks them as a player again
     * @param player ArenaPlayer
     */
    default void stopSpectating(ArenaPlayer player) {
        if (!getSpectators().contains(player)) {
            return;
        }

        player.getPlayer().ifPresent(bukkitPlayer -> {
            player.setCurrentState(EPlayerState.LOBBY);

            // TODO: Teleport to spawn
            bukkitPlayer.setGameMode(GameMode.SURVIVAL);
            Players.resetHealth(bukkitPlayer);

            getSpectators().remove(player);
        });
    }

    /**
     * @deprecated Use sendMessage#Component
     * @param message
     */
    default void sendMessage(String message) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                player.sendMessage(message);
            }
        });
    }

    /**
     * @deprecated Use sendMessage#Component
     * @param baseComponent
     */
    default void sendMessage(BaseComponent baseComponent) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                player.spigot().sendMessage(baseComponent);
            }
        });
    }

    /**
     * @deprecated Use sendMessage#Component
     * @param parts
     */
    default void sendMessage(BaseComponent[] parts) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                player.spigot().sendMessage(parts);
            }
        });
    }

    default void sendMessage(Component component) {
        getPlayers().forEach(arenaPlayer -> arenaPlayer.getPlayer().ifPresent(player -> player.sendMessage(component)));
    }

    default void sendSound(Sound sound) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                Players.playSound(player, sound);
            }
        });
    }

    default void sendTitle(String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        getPlayers().forEach(arenaPlayer -> {
            final Player player = Bukkit.getPlayer(arenaPlayer.getUniqueId());

            if (player != null) {
                player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
            }
        });
    }

    default void saveStats(ArenaPlayer arenaPlayer) {
        if (arenaPlayer.getStatHolder() == null) {
            throw new NullPointerException("Player Stat Holder is null");
        }

        getFinalStats().removeIf(existing -> existing.getOwner().equals(arenaPlayer.getUniqueId()));
        getFinalStats().add(arenaPlayer.getStatHolder());
    }
}
