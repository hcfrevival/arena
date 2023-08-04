package net.hcfrevival.arena.level.impl;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;

import java.util.List;
import java.util.UUID;

public class TeamArenaInstance extends DuelArenaInstance {
    public TeamArenaInstance(UUID uniqueId, boolean available, List<PLocatable> spawnpoints, PLocatable spectatorSpawnpoint, IRegion region) {
        super(uniqueId, available, spawnpoints, spectatorSpawnpoint, region);
    }
}
