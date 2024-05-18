package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.events.impl.PlayerDamagePlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.team.TeamManager;
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

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> teamManager.getTeam(player).ifPresent(team -> team.removeMember(arenaPlayer)));
    }

    @EventHandler
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        Player attacker = event.getDamager();
        Player attacked = event.getDamaged();
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(attacker).ifPresent(team -> {
            if (team.isMember(attacked.getUniqueId())) {
                event.setCancelled(true);
            }
        });
    }
}
