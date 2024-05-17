package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public final class DisbandTeamItem implements ICustomItem {
    @Getter public ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.BARRIER;
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Disband Team" + ChatColor.GRAY + " (Right-click)";
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "DisbandTeamItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Disband Team", NamedTextColor.RED)
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
    public boolean isSoulbound() {
        return true;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            final TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
            final Optional<Team> teamQuery = teamManager.getTeam(who);

            if (teamQuery.isEmpty()) {
                who.sendMessage(ChatColor.RED + "Team not found");
                return;
            }

            final Team team = teamQuery.get();

            if (!team.getLeader().getUniqueId().equals(who.getUniqueId())) {
                who.sendMessage(ChatColor.RED + "You are not the leader of the team");
                return;
            }

            teamManager.disbandTeam(team);
        };
    }
}
