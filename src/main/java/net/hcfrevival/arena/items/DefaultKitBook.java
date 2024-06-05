package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
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

public record DefaultKitBook(@Getter ArenaPlugin plugin) implements ICustomItem {
    @Override
    public Material getMaterial() {
        return Material.ENCHANTED_BOOK;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Default" + ChatColor.GRAY + " (Right-click)";
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "DefaultKitBook");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Default", NamedTextColor.YELLOW)
                .appendSpace().append(Component.text("(", NamedTextColor.GRAY).append(Component.keybind("key.use").append(Component.text(")"))));
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
    public boolean isRepairable() {
        return true;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
            final KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);
            final TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

            sessionManager.getSession(who).ifPresent(session -> {
                if (session.getGamerule().equals(EGamerule.HCF)) {
                    TeamLoadoutConfig.ELoadoutValue value = teamManager.getTeam(who).map(team ->
                            team.getLoadoutConfig().getLoadoutValue(who)).orElse(TeamLoadoutConfig.ELoadoutValue.NETHERITE);

                    kitManager.getDefaultKit(value).ifPresent(defaultKit -> defaultKit.apply(who, true));
                    return;
                }

                kitManager.getDefaultKit(session.getGamerule()).ifPresent(defaultKit -> defaultKit.apply(who, true));
            });
        };
    }

    @Override
    public ItemStack getItem() {
        final ItemStack item = ICustomItem.super.getItem();
        final ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        }

        item.setItemMeta(meta);
        return item;
    }
}
