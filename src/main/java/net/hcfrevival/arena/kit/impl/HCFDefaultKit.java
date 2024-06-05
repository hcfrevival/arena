package net.hcfrevival.arena.kit.impl;

import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class HCFDefaultKit extends DefaultKit {
    public final TeamLoadoutConfig.ELoadoutValue type;

    public HCFDefaultKit(TeamLoadoutConfig.ELoadoutValue type, List<ItemStack> contents) {
        super(EGamerule.HCF, contents);
        this.type = type;
    }
}
