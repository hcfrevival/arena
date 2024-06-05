package net.hcfrevival.arena.util;

import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import gg.hcfactions.libs.bukkit.utils.Players;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.items.*;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class LobbyUtil {
    public static void giveLobbyItems(ArenaPlugin plugin, Player player) {
        CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
        Optional<Team> teamQuery = teamManager.getTeam(player);

        Players.resetHealth(player);
        player.getInventory().clear();

        if (teamQuery.isPresent()) {
            ArenaPlayer arenaPlayer = playerManager.getPlayer(player.getUniqueId()).orElseThrow(NullPointerException::new);
            Team team = teamQuery.get();

            if (team.getLeader().equals(arenaPlayer)) {
                givePartyLeaderItems(plugin, player);
            } else {
                givePartyMemberItems(plugin, player);
            }

            return;
        }

        cis.getItem(UnrankedQueueItem.class).ifPresent(item -> player.getInventory().setItem(0, item.getItem()));
        cis.getItem(RankedQueueItem.class).ifPresent(item -> player.getInventory().setItem(1, item.getItem()));
        cis.getItem(CreatePartyItem.class).ifPresent(item -> player.getInventory().setItem(4, item.getItem()));
        cis.getItem(EditKitItem.class).ifPresent(item -> player.getInventory().setItem(6, item.getItem()));
    }

    public static void giveQueueItems(ArenaPlugin plugin, Player player) {
        Players.resetHealth(player);
        player.getInventory().clear();

        final CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        cis.getItem(LeaveQueueItem.class).ifPresent(leaveQueueItem -> player.getInventory().setItem(4, leaveQueueItem.getItem()));
    }

    public static void givePartyLeaderItems(ArenaPlugin plugin, Player player) {
        CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        cis.getItem(DisbandTeamItem.class).ifPresent(disbandItem -> player.getInventory().setItem(8, disbandItem.getItem()));
        cis.getItem(TeamListItem.class).ifPresent(teamListItem -> player.getInventory().setItem(4, teamListItem.getItem()));
        cis.getItem(TeamLoadoutItem.class).ifPresent(teamLoadoutItem -> player.getInventory().setItem(0, teamLoadoutItem.getItem()));
    }

    public static void givePartyMemberItems(ArenaPlugin plugin, Player player) {
        CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        cis.getItem(LeaveTeamItem.class).ifPresent(leaveTeamItem -> player.getInventory().setItem(8, leaveTeamItem.getItem()));
        cis.getItem(TeamListItem.class).ifPresent(teamListItem -> player.getInventory().setItem(4, teamListItem.getItem()));
    }
}
