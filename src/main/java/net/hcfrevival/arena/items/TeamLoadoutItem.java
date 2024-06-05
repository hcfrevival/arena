package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.menu.TeamLoadoutMenu;
import net.hcfrevival.arena.team.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class TeamLoadoutItem implements ICustomItem {
    @Getter public final ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.WRITTEN_BOOK;
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "TeamLoadoutItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Team Loadout", NamedTextColor.YELLOW)
                .appendSpace().append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.keybind("key.use", NamedTextColor.GRAY))
                .append(Component.text(")", NamedTextColor.GRAY));
    }

    @Override
    public List<Component> getLoreComponents() {
        List<Component> res = Lists.newArrayList();
        res.add(Component.text("Edit your team's HCF loadouts", NamedTextColor.GRAY));
        return res;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

            teamManager.getTeam(who).ifPresentOrElse(team -> {
                if (!team.getLeader().getUniqueId().equals(who.getUniqueId())) {
                    who.sendMessage(Component.text("You are not the team leader", NamedTextColor.RED));
                    return;
                }

                TeamLoadoutMenu menu = new TeamLoadoutMenu(plugin, who, team);
                menu.open();
            }, () -> who.sendMessage(Component.text("You are not on a team", NamedTextColor.RED)));
        };
    }
}
