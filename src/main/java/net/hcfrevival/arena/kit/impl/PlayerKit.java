package net.hcfrevival.arena.kit.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.IArenaKit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PlayerKit implements IArenaKit {
    @Getter public final UUID ownerId;
    @Getter public final EGamerule gamerule;
    @Getter public final List<ItemStack> contents;

    @Override
    public String getName() {
        return gamerule.getDisplayName() + ChatColor.YELLOW + " (Custom)";
    }
}
