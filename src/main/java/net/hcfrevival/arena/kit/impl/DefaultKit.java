package net.hcfrevival.arena.kit.impl;

import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.IArenaKit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record DefaultKit(@Getter EGamerule gamerule, @Getter List<ItemStack> contents,
                         @Getter List<ItemStack> armorContents) implements IArenaKit {
    @Override
    public String getName() {
        return gamerule.getDisplayName() + ChatColor.YELLOW + " Default";
    }
}
