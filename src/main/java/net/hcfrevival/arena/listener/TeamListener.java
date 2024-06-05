package net.hcfrevival.arena.listener;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.events.impl.PlayerDamagePlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.TeamMatchFinishEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.arena.util.LobbyUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

@AllArgsConstructor
public final class TeamListener implements Listener {
    @Getter public final ArenaPlugin plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(player).ifPresent(team -> {
            playerManager.getPlayer(player.getUniqueId()).ifPresent(team::removeMember);

            if (team.getLeader().getUniqueId().equals(player.getUniqueId())) {
                team.getNewLeader().ifPresentOrElse(newLeader -> {
                    team.setLeader(newLeader);
                    team.getMembers().remove(newLeader);

                    newLeader.getPlayer().ifPresent(leaderPlayer -> LobbyUtil.giveLobbyItems(plugin, leaderPlayer));

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

    @EventHandler
    public void onMatchFinish(TeamMatchFinishEvent event) {
        List<Team> teams = Lists.newArrayList();
        teams.addAll(event.getLosers());
        teams.add(event.getWinner());
        teams.forEach(team -> team.getLoadoutConfig().performCleanup());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if (!(proj.getShooter() instanceof Player shooter)) {
            return;
        }

        if (!(event.getHitEntity() instanceof Player hitEntity)) {
            return;
        }

        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(shooter).ifPresent(team -> {
            if (team.isMember(hitEntity.getUniqueId())) {
                event.setCancelled(true);
            }
        });
    }
}
