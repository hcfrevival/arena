package net.hcfrevival.arena.level.builder;

import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.level.impl.ArenaRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ILevelBuilder<T> {
    UUID getBuilderId();
    IArena getOwner();
    List<PLocatable> getSpawnPositions();
    PLocatable getSpectatorSpawnPosition();
    ArenaRegion getArenaRegion();

    default Optional<Player> getPlayer() {
        Player player = Bukkit.getPlayer(getBuilderId());

        if (player == null) {
            return Optional.empty();
        }

        return Optional.of(player);
    }

    default void addSpawnPosition(PLocatable location) {
        getSpawnPositions().add(location);
    }

    default void setArenaRegionCorner(BLocatable location, ERegionCorner corner) {
        if (corner.equals(ERegionCorner.A)) {
            getArenaRegion().setCornerA(location);
            return;
        }

        getArenaRegion().setCornerB(location);
    }

    default boolean isReadyToBuild() {
        if (getOwner() == null) {
            return false;
        }

        if (getSpawnPositions().isEmpty()) {
            return false;
        }

        if (getSpectatorSpawnPosition() == null) {
            return false;
        }

        if (getArenaRegion() == null || getArenaRegion().getCornerA() == null || getArenaRegion().getCornerB() == null) {
            return false;
        }

        return true;
    }

    void setOwner(IArena arena);

    void setSpectatorSpawnPosition(PLocatable locatable);

    T build();
}
