package net.hcfrevival.arena.inventory;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StoredInventory {
    @Getter public final UUID owner;
    @Getter public final String username;
    @Getter public final double health;
    @Getter public final int foodLevel;
    @Getter public final List<ItemStack> contents;
    @Getter public final List<PotionEffect> potionEffects;

    public StoredInventory(Player player) {
        this.owner = player.getUniqueId();
        this.username = player.getName();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.contents = Arrays.asList(player.getInventory().getContents());
        this.potionEffects = Lists.newArrayList(player.getActivePotionEffects());
    }
}
