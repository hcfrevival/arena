package net.hcfrevival.arena.stats.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.stats.IStatHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public final class PlayerStatHolder implements IStatHolder {
    @Getter public final UUID owner;
    @Getter @Setter public long sprintResetHits;
    @Getter @Setter public long totalDamage;
    @Getter @Setter public long biggestCombo;
    @Getter @Setter public long currentCombo;
    @Getter @Setter public long totalHits;
    @Getter @Setter public double health;
    @Getter @Setter public int foodLevel;
    @Getter public final List<ItemStack> contents;
    @Getter public final List<PotionEffect> potionEffects;
    @Getter public final List<Double> potionAccuracyValues;

    public PlayerStatHolder(UUID owner) {
        this.owner = owner;
        this.sprintResetHits = 0;
        this.totalDamage = 0;
        this.biggestCombo = 0;
        this.currentCombo = 0;
        this.totalHits = 0;
        this.health = 0.0;
        this.foodLevel = 0;
        this.contents = Lists.newArrayList();
        this.potionEffects = Lists.newArrayList();
        this.potionAccuracyValues = Lists.newArrayList();
    }

    public void storeFinalAttributes(Player player) {
        health = player.getHealth();
        foodLevel = player.getFoodLevel();
        potionEffects.addAll(player.getActivePotionEffects());

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                contents.add(new ItemStack(Material.AIR, 1));
                continue;
            }

            contents.add(item);
        }
    }

    @Override
    public String toString() {
        return "owner: " + owner.toString()
                + "\nsprintResetHits: " + sprintResetHits
                + "\ntotalDamage: " + totalDamage
                + "\nbiggestCombo: " + biggestCombo
                + "\ncurrentCombo: " + currentCombo
                + "\ntotalHits: " + totalHits
                + "\nhealth: " + health
                + "\nfoodLevel: " + foodLevel
                + "\ncontents: " + contents.size()
                + "\npotionEffects: " + potionEffects.toString()
                + "\npotionAccuracy: " + getPotionAccuracy();
    }
}
