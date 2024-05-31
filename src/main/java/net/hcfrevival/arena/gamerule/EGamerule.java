package net.hcfrevival.arena.gamerule;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.awt.*;

@AllArgsConstructor
public enum EGamerule {
    NODEBUFF(net.kyori.adventure.text.Component.text("No Debuff", NamedTextColor.RED), ChatColor.RED + "No Debuff", Material.SPLASH_POTION),
    DEBUFF(net.kyori.adventure.text.Component.text("Debuff", TextColor.color(PotionEffectType.POISON.getColor().getRed(), PotionEffectType.POISON.getColor().getGreen(), PotionEffectType.POISON.getColor().getBlue())), net.md_5.bungee.api.ChatColor.of(String.format("#%02x%02x%02x", PotionType.POISON.getEffectType().getColor().getRed(), PotionType.POISON.getEffectType().getColor().getGreen(), PotionType.POISON.getEffectType().getColor().getBlue())) + "Debuff", Material.SPLASH_POTION),
    // GAPPLE(net.kyori.adventure.text.Component.text("Gapple", NamedTextColor.LIGHT_PURPLE), ChatColor.LIGHT_PURPLE + "Gapple", Material.ENCHANTED_GOLDEN_APPLE),
    VANILLA(net.kyori.adventure.text.Component.text("Vanilla", NamedTextColor.DARK_PURPLE), ChatColor.DARK_PURPLE + "Vanilla", Material.NETHERITE_SWORD);
    //MACE(net.kyori.adventure.text.Component.text("Mace", NamedTextColor.DARK_GRAY), ChatColor.DARK_GRAY + "Mace", Material.MACE);

    @Getter public Component displayNameComponent;
    @Getter public String displayName;
    @Getter public Material icon;

    public static ItemStack getIcon(EGamerule rule) {
        final ItemStack item = new ItemBuilder()
                .setMaterial(rule.getIcon())
                .setName(rule.getDisplayNameComponent())
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                .build();

        if (rule.equals(NODEBUFF) || rule.equals(DEBUFF)) {
            final PotionMeta meta = (PotionMeta) item.getItemMeta();
            final Color potionColor = (rule.equals(NODEBUFF) ? PotionType.HEALING.getEffectType().getColor() : PotionType.POISON.getEffectType().getColor());

            if (meta != null) {
                meta.setColor(potionColor);
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public static ItemStack getIcon(EGamerule rule, int inQueue) {
        final ItemStack item = new ItemBuilder()
                .setMaterial(rule.getIcon())
                .setName(rule.getDisplayNameComponent())
                .addLoreComponents(Lists.newArrayList(Component.text("In-Queue:", NamedTextColor.WHITE).appendSpace().append(Component.text(inQueue, NamedTextColor.AQUA))))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                .build();

        if (rule.equals(NODEBUFF) || rule.equals(DEBUFF)) {
            final PotionMeta meta = (PotionMeta) item.getItemMeta();
            final Color potionColor = (rule.equals(NODEBUFF) ? PotionType.HEALING.getEffectType().getColor() : PotionType.POISON.getEffectType().getColor());

            if (meta != null) {
                meta.setColor(potionColor);
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
        }

        return item;
    }
}
