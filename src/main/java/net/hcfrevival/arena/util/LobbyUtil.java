package net.hcfrevival.arena.util;

import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import gg.hcfactions.libs.bukkit.utils.Players;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.items.*;
import org.bukkit.entity.Player;

public final class LobbyUtil {
    public static void giveLobbyItems(ArenaPlugin plugin, Player player) {
        Players.resetHealth(player);
        player.getInventory().clear();

        final CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);

        cis.getItem(UnrankedQueueItem.class).ifPresent(unrankedQueueItem -> player.getInventory().setItem(0, unrankedQueueItem.getItem()));
        cis.getItem(RankedQueueItem.class).ifPresent(unrankedQueueItem -> player.getInventory().setItem(1, unrankedQueueItem.getItem()));
        cis.getItem(CreatePartyItem.class).ifPresent(unrankedQueueItem -> player.getInventory().setItem(4, unrankedQueueItem.getItem()));
        cis.getItem(EditKitItem.class).ifPresent(unrankedQueueItem -> player.getInventory().setItem(6, unrankedQueueItem.getItem()));
    }

    public static void giveQueueItems(ArenaPlugin plugin, Player player) {
        Players.resetHealth(player);
        player.getInventory().clear();

        final CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        cis.getItem(LeaveQueueItem.class).ifPresent(leaveQueueItem -> player.getInventory().setItem(4, leaveQueueItem.getItem()));
    }

    public static void givePartyLeaderItems(ArenaPlugin plugin, Player player) {
        Players.resetHealth(player);
        player.getInventory().clear();

        final CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        cis.getItem(DisbandTeamItem.class).ifPresent(disbandItem -> player.getInventory().setItem(8, disbandItem.getItem()));
        cis.getItem(TeamListItem.class).ifPresent(teamListItem -> player.getInventory().setItem(4, teamListItem.getItem()));
    }

    public static void givePartyMemberItems(ArenaPlugin plugin, Player player) {

    }
}
