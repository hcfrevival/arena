package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.menu.TeamListMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class TeamListItem implements ICustomItem {
    @Getter public final ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "Team List" + ChatColor.GRAY + " (Right-click)";
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "TeamListItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Team List", NamedTextColor.AQUA)
                .appendSpace().append(Component.text("(", NamedTextColor.GRAY).append(Component.keybind("key.use").append(Component.text(")"))));
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
    public Runnable getRightClick(Player who) {
        return () -> {
            final TeamListMenu menu = new TeamListMenu(plugin, who);
            menu.open();
        };
    }
}
