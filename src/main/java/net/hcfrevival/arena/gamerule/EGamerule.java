package net.hcfrevival.arena.gamerule;

import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

@AllArgsConstructor
public enum EGamerule {
    NODEBUFF(ChatColor.RED + "No Debuff", Material.SPLASH_POTION),
    DEBUFF(net.md_5.bungee.api.ChatColor.of(String.format("#%02x%02x%02x", PotionType.POISON.getEffectType().getColor().getRed(), PotionType.POISON.getEffectType().getColor().getGreen(), PotionType.POISON.getEffectType().getColor().getBlue())) + "Debuff", Material.SPLASH_POTION),
    GAPPLE(ChatColor.LIGHT_PURPLE + "Gapple", Material.ENCHANTED_GOLDEN_APPLE),
    VANILLA(ChatColor.DARK_PURPLE + "Vanilla", Material.NETHERITE_SWORD);

    @Getter public String displayName;
    @Getter public Material icon;

    public static ItemStack getIcon(EGamerule rule) {
        final ItemStack item = new ItemBuilder()
                .setMaterial(rule.getIcon())
                .setName(rule.getDisplayName())
                .addFlag(ItemFlag.HIDE_ATTRIBUTES).build();

        if (rule.equals(NODEBUFF) || rule.equals(DEBUFF)) {
            final PotionMeta meta = (PotionMeta) item.getItemMeta();
            final Color potionColor = (rule.equals(NODEBUFF) ? PotionType.INSTANT_HEAL.getEffectType().getColor() : PotionType.POISON.getEffectType().getColor());

            if (meta != null) {
                meta.setColor(potionColor);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
        }

        return item;
    }
}
