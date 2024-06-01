package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.services.impl.ranks.RankService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public final class CosmeticListener implements Listener {
    @Getter public final ArenaPlugin plugin;

    @EventHandler
    public void onPlayerSetDisplayName(PlayerJoinEvent event) {
        RankService rankService = (RankService) plugin.getService(RankService.class);

        if (rankService == null) {
            return;
        }

        Player player = event.getPlayer();
        player.playerListName(rankService.getFormattedNameComponent(player));
    }
}
