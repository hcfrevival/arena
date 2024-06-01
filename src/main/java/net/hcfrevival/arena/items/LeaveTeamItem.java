package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.util.LobbyUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class LeaveTeamItem implements ICustomItem {
    @Getter public final ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.BARRIER;
    }

    @Override
    public Map.Entry<NamespacedKey, String> getIdentifier() {
        return Map.entry(plugin.getNamespacedKey(), "LeaveTeamItem");
    }

    @Override
    public Component getDisplayNameComponent() {
        return Component.text("Leave Team", NamedTextColor.RED)
                .appendSpace().append(Component.text("(", NamedTextColor.GRAY).append(Component.keybind("key.use")).append(Component.text(")", NamedTextColor.GRAY)));
    }

    @Override
    public List<Component> getLoreComponents() {
        List<Component> res = Lists.newArrayList();
        res.add(Component.text("Leave your current team", NamedTextColor.GRAY));
        return res;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
            TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
            ArenaPlayer arenaPlayer = playerManager.getPlayer(who.getUniqueId()).orElseThrow(NullPointerException::new);

            arenaPlayer.clearFriendlies();

            teamManager.getTeam(who).ifPresent(team -> {
                team.removeMember(arenaPlayer);
                LobbyUtil.giveLobbyItems(plugin, who);
            });
        };
    }
}
