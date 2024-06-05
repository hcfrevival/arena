package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class TeamLoadoutMenu extends GenericMenu {
    @Getter public final ArenaPlugin plugin;
    @Getter public final Team team;

    public TeamLoadoutMenu(ArenaPlugin plugin, Player player, Team team) {
        super(plugin, player, "Team Loadout", 6);
        this.plugin = plugin;
        this.team = team;
    }

    @Override
    public void open() {
        super.open();

        List<ArenaPlayer> members = team.getFullMembers();
        for (int i = 0; i < members.size(); i++) {
            ArenaPlayer teamMember = members.get(i);
            TeamLoadoutConfig.ELoadoutValue currentValue = team.getLoadoutConfig().getLoadoutValue(teamMember.getUniqueId());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            ItemMeta meta = skull.getItemMeta();

            if (meta != null) {
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                List<Component> lore = Lists.newArrayList();

                skullMeta.displayName(Component.text(teamMember.getUsername(), NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(teamMember.getUniqueId()));

                lore.add(Component.text("Current Loadout", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .append(Component.text(": " + Strings.capitalize(currentValue.name().toLowerCase()), NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                skullMeta.lore(lore);

                skull.setItemMeta(skullMeta);

                addItem(new Clickable(skull, i, click -> {
                    PlayerLoadoutMenu loadoutMenu = new PlayerLoadoutMenu(plugin, player, team, teamMember);
                    loadoutMenu.open();
                }));
            }
        }
    }
}
