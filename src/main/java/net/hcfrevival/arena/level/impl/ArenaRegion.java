package net.hcfrevival.arena.level.impl;

import gg.hcfactions.libs.bukkit.location.IRegion;
import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public final class ArenaRegion implements IRegion {
    public BLocatable cornerA;
    public BLocatable cornerB;
}
