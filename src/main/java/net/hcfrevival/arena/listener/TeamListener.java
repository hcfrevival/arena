package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.events.impl.PlayerDamagePlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.team.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public final class TeamListener implements Listener {
    @Getter public final ArenaPlugin plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(player).ifPresent(team -> {
            plugin.getAresLogger().info("Found team");
            playerManager.getPlayer(player.getUniqueId()).ifPresent(team::removeMember);

            if (team.getLeader().getUniqueId().equals(player.getUniqueId())) {
                plugin.getAresLogger().info("Found leader");
                team.getNewLeader().ifPresentOrElse(newLeader -> {
                    team.setLeader(newLeader);
                    team.getMembers().remove(newLeader);

                    team.sendMessage(Component.text(newLeader.getUsername(), NamedTextColor.AQUA)
                            .appendSpace().append(Component.text("has been appointed as the new team leader", NamedTextColor.GRAY)));
                }, () -> {
                    plugin.getAresLogger().info("Team disbanded");
                    team.sendMessage(Component.text("Team disbanded", NamedTextColor.YELLOW));
                    team.disband();
                    teamManager.getTeamRepository().remove(team);
                });
            }
        });
    }

    @EventHandler
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        Player attacker = event.getDamager();
        Player attacked = event.getDamaged();
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        if (attacker.getUniqueId().equals(attacked.getUniqueId())) {
            return;
        }

        teamManager.getTeam(attacker).ifPresent(team -> {
            if (team.isMember(attacked.getUniqueId())) {
                attacker.sendMessage(Component.text("You can not attack your team", NamedTextColor.RED));
                event.setCancelled(true);
            }
        });
    }
}
