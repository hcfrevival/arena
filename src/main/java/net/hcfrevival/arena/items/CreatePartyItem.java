package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
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
import java.util.Optional;

@AllArgsConstructor
public final class CreatePartyItem implements ICustomItem {
    @Getter public ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.NAME_TAG;
    }

    @Override
    public String getName() {
        return ChatColor.GREEN + "Create Party";
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "CreatePartyItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Create Party", NamedTextColor.GREEN);
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
        return () -> {
            final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
            final TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
            final Optional<ArenaPlayer> playerQuery = playerManager.getPlayer(who.getUniqueId());

            if (playerQuery.isEmpty()) {
                who.sendMessage(ChatColor.RED + "Failed to load your Arena Player data");
                return;
            }

            teamManager.createTeam(playerQuery.get());
        };
    }
}

