package net.hcfrevival.arena.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public final class PlayerUtil {
    /**
     * Returns a count for how many health potions are in a provided inventory
     * @param inventory Inventory to count from
     * @return Integer
     */
    public static int countHealthPotions(Inventory inventory) {
        int count = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null) {
                continue;
            }

            if (!item.getType().equals(Material.POTION)
                && !item.getType().equals(Material.SPLASH_POTION)
                && !item.getType().equals(Material.LINGERING_POTION)
            ) {
                continue;
            }

            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            if (potionMeta == null) {
                continue;
            }

            if (potionMeta.getBasePotionType() == null) {
                continue;
            }

            if (!potionMeta.getBasePotionType().getPotionEffects().stream().anyMatch(eff -> eff.getType().equals(PotionEffectType.INSTANT_HEALTH))) {
                continue;
            }

            count += 1;
        }

        return count;
    }
}
