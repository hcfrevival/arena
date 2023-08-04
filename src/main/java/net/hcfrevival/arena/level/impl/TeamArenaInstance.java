package net.hcfrevival.arena.level.impl;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import net.hcfrevival.arena.level.IArena;

import java.util.List;
import java.util.UUID;

public class TeamArenaInstance extends DuelArenaInstance {
    public TeamArenaInstance(UUID uniqueId, TeamArena owner, boolean available, List<PLocatable> spawnpoints, PLocatable spectatorSpawnpoint, IRegion region) {
        super(uniqueId, owner, available, spawnpoints, spectatorSpawnpoint, region);
    }

    @Override
    public void setOwner(IArena owner) {
        this.owner = (TeamArena) owner;
    }
}
