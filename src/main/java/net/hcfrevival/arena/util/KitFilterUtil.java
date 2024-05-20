package net.hcfrevival.arena.util;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.base.util.Strings;
import net.hcfrevival.arena.gamerule.EGamerule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class KitFilterUtil {
    public static void isValid(List<ItemStack> contents, EGamerule gamerule, Promise promise) {
        List<ItemStack> potions = contents.stream().filter(item -> item != null && (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || item.getType().equals(Material.LINGERING_POTION))).toList();
        List<PotionEffectType> requiredPotionTypes = Lists.newArrayList();

        if (gamerule.equals(EGamerule.NODEBUFF) || gamerule.equals(EGamerule.DEBUFF)) {
            requiredPotionTypes.add(PotionEffectType.SPEED);
        }

        if (gamerule.equals(EGamerule.VANILLA)) {
            requiredPotionTypes.add(PotionEffectType.SPEED);
            requiredPotionTypes.add(PotionEffectType.STRENGTH);
            requiredPotionTypes.add(PotionEffectType.REGENERATION);
        }

        if (!requiredPotionTypes.isEmpty()) {
            for (PotionEffectType requiredType : requiredPotionTypes) {
                int count = 0;

                for (ItemStack potionItem : potions) {
                    PotionMeta meta = (PotionMeta) potionItem.getItemMeta();

                    if (meta.getBasePotionType() == null) {
                        continue;
                    }

                    if (meta.getBasePotionType().getPotionEffects().stream().anyMatch(eff -> eff.getType().equals(requiredType))) {
                        count += 1;
                    }
                }

                if (count < 4) {
                    promise.reject(
                            "At least 4 "
                                    + Strings.capitalize(requiredType.getKey().getKey().toLowerCase().replaceAll("_", " "))
                                    + " Potions are required for this kit"
                    );

                    return;
                }
            }
        }

        promise.resolve();
    }
}
