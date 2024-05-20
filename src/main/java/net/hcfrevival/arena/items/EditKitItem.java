package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.menu.KitEditorMenu;
import net.hcfrevival.arena.menu.KitSelectMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public final class EditKitItem implements ICustomItem {
    public ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.DIAMOND_HELMET;
    }

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "Edit Kits";
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "EditKitItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Edit Kits", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public List<String> getLore() {
        return Lists.newArrayList();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
    }

    @Override
    public boolean isRepairable() {
        return true;
    }

    @Override
    public ItemStack getItem() {
        final ItemStack item = ICustomItem.super.getItem();
        final ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            KitSelectMenu selectMenu = new KitSelectMenu(plugin, who, new FailablePromise<>() {
                @Override
                public void resolve(EGamerule eGamerule) {
                    new Scheduler(plugin).sync(() -> {
                        KitEditorMenu editorMenu = new KitEditorMenu(plugin, who, eGamerule);
                        editorMenu.open();
                    }).delay(1L).run();
                }

                @Override
                public void reject(String s) {
                    who.sendMessage(Component.text("An error has occurred. Please try again later.", NamedTextColor.RED));
                }
            });

            selectMenu.open();
        };
    }
}

