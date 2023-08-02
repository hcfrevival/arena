package net.hcfrevival.arena.gamerule;

import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum EGamerule {
    NODEBUFF(ChatColor.GREEN + "HCF (No Debuff)", Material.DIAMOND_HELMET),
    DEBUFF(ChatColor.RED + "HCF (Debuff)", Material.NETHERITE_HELMET),
    GAPPLE(ChatColor.LIGHT_PURPLE + "Gapple", Material.ENCHANTED_GOLDEN_APPLE),
    VANILLA(ChatColor.DARK_PURPLE + "Vanilla", Material.NETHERITE_SWORD);

    @Getter public String displayName;
    @Getter public Material icon;

    public static ItemStack getIcon(EGamerule rule) {
        return new ItemBuilder()
                .setMaterial(rule.getIcon())
                .setName(rule.getDisplayName())
                .addFlag(ItemFlag.HIDE_ATTRIBUTES).build();
    }
}
