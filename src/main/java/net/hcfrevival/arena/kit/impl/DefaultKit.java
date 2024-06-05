package net.hcfrevival.arena.kit.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.IArenaKit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Getter
public class DefaultKit implements IArenaKit {
    EGamerule gamerule;
    List<ItemStack> contents;

    @Override
    public String getName() {
        return gamerule.getDisplayName() + ChatColor.YELLOW + " Default";
    }
}
