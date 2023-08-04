package net.hcfrevival.arena.session;

import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.utils.Players;
import net.hcfrevival.arena.level.IArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import org.bukkit.GameMode;

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

            getSpectators().add(player);
        });
    }
}
