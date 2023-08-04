package net.hcfrevival.arena.level;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;

import java.util.List;
import java.util.UUID;

public interface IArenaInstance {
    /**
     * @return Arena UUID
     */
    UUID getUniqueId();

    /**
     * @return Owning Arena Container
     */
    IArena getOwner();

    /**
     * @return If true, this arena can be claimed
     */
    boolean isAvailable();

    /**
     * @return List of Spawnpoints
     */
    List<PLocatable> getSpawnpoints();

    /**
     * @return Spectator Spawnpoint
     */
    PLocatable getSpectatorSpawnpoint();

    /**
     * @return Region this arena persists of, preventing any players from exiting it
     * while they are in the session
     */
    IRegion getRegion();

    /**
     * @param b Set the Arena Instance availability state
     */
    void setAvailable(boolean b);

    /**
     * @param owner Arena owner
     */
    void setOwner(IArena owner);
}
