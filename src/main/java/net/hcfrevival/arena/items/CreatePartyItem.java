package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public final class CreatePartyItem implements ICustomItem {
    @Override
    public Material getMaterial() {
        return Material.NAME_TAG;
    }

    @Override
    public String getName() {
        return ChatColor.GREEN + "Create Party";
    }

    @Override
    public List<String> getLore() {
        final List<String> res = Lists.newArrayList();
        res.add(ChatColor.GRAY + "Create a party and invite your friends");
        res.add(ChatColor.GRAY + "to compete against others.");
        return res;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
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
        return () -> who.sendMessage("createParty");
    }
}

