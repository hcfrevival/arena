package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.session.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public record CustomKitBook(@Getter ArenaPlugin plugin) implements ICustomItem {
    @Override
    public Material getMaterial() {
        return Material.ENCHANTED_BOOK;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Custom" + ChatColor.GRAY + " (Right-click)";
    }

    @Override
    public List<String> getLore() {
        return Lists.newArrayList();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        final Map<Enchantment, Integer> enchantments = Maps.newHashMap();
        enchantments.put(Enchantment.BINDING_CURSE, 1);
        return enchantments;
    }

    @Override
    public boolean isSoulbound() {
        return true;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
            final KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);

            sessionManager.getSession(who).flatMap(session ->
                    kitManager.getPlayerKit(who, session.getGamerule())).ifPresent(playerKit ->
                        playerKit.apply(who, true));
        };
    }
}
