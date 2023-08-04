package net.hcfrevival.arena.level.impl;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.level.IArenaInstance;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class DuelArenaInstance implements IArenaInstance {
    @Getter public final UUID uniqueId;
    @Getter @Setter public boolean available;
    @Getter public final List<PLocatable> spawnpoints;
    @Getter public final PLocatable spectatorSpawnpoint;
    @Getter public final IRegion region;
}
