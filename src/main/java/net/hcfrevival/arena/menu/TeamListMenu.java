package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class TeamListMenu extends GenericMenu {
    private final ArenaPlugin plugin;

    public TeamListMenu(ArenaPlugin plugin, Player player) {
        super(plugin, player, "Teams", 6);
        this.plugin = plugin;
    }

    @Override
    public void open() {
        final TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
        int cursor = 0;

        // TODO: Moving forward, this should only query teams in-lobby
        for (Team team : teamManager.getTeamRepository()) {
            final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            final ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                final SkullMeta skullMeta = (SkullMeta) meta;

                final List<String> lore = Lists.newArrayList();
                team.getFullMembers().forEach(member -> lore.add(ChatColor.YELLOW + member.getUsername()));
                skullMeta.setLore(lore);

                skullMeta.setDisplayName(ChatColor.GOLD + team.getDisplayName() + (team.isMember(player.getUniqueId())
                        ? " " + ChatColor.GRAY + "(You)"
                        : " (" + team.getFullMembers().size() + ")"));

                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(team.getLeader().getUniqueId()));
                item.setItemMeta(skullMeta);
            }

            addItem(new Clickable(item, cursor, click -> {
                // TODO: Open Duel Request GUI
                player.sendMessage("openDuelRequestGUI");
            }));

            cursor += 1;
        }

        super.open();
    }
}
