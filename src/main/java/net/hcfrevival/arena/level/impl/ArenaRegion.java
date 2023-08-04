package net.hcfrevival.arena.level.impl;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class ArenaRegion implements IRegion {
    @Getter public final BLocatable cornerA;
    @Getter public final BLocatable cornerB;
}
