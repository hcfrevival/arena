package net.hcfrevival.arena.kit.impl;

import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
public class HCFPlayerKit extends PlayerKit {
    public final TeamLoadoutConfig.ELoadoutValue type;

    public HCFPlayerKit(TeamLoadoutConfig.ELoadoutValue type, UUID ownerId, List<ItemStack> contents) {
        super(ownerId, EGamerule.HCF, contents);
        this.type = type;
    }
}
