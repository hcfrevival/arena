package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.session.request.impl.TeamDuelRequest;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Optional;

public final class TeamListMenu extends GenericMenu {
    private final ArenaPlugin plugin;

    public TeamListMenu(ArenaPlugin plugin, Player player) {
        super(plugin, player, "Teams", 6);
        this.plugin = plugin;
    }

    @Override
    public void open() {
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        Optional<Team> selfQuery = teamManager.getTeam(player);
        int cursor = 0;

        if (selfQuery.isEmpty()) {
            player.sendMessage(Component.text("Team not found", NamedTextColor.RED));
            return;
        }

        Team self = selfQuery.get();

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
                if (self.equals(team)) {
                    return;
                }

                KitSelectMenu kitSelectMenu = new KitSelectMenu(plugin, player, new FailablePromise<>() {
                    @Override
                    public void resolve(EGamerule eGamerule) {
                        sessionManager.getDuelRequestManager().getExecutor().send(self, team, eGamerule, new Promise() {
                            @Override
                            public void resolve() {
                                open();
                            }

                            @Override
                            public void reject(String s) {
                                open();
                                self.sendMessage(Component.text("Unable to send duel request: " + s, NamedTextColor.RED));
                            }
                        });
                    }

                    @Override
                    public void reject(String s) {
                        player.sendMessage(Component.text("Failed to process selection: " + s, NamedTextColor.RED));
                    }
                });

                player.closeInventory();
                kitSelectMenu.open();
            }));

            cursor += 1;
        }

        super.open();
    }
}
