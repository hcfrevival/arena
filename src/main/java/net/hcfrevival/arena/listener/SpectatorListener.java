package net.hcfrevival.arena.listener;

import gg.hcfactions.libs.bukkit.events.impl.PlayerBigMoveEvent;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.IArenaInstance;
import net.hcfrevival.arena.session.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public record SpectatorListener(@Getter ArenaPlugin plugin) implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerBigMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);

        sessionManager.getSession(player).ifPresent(session -> {
            final IArenaInstance arenaInstance = session.getArena();

            if (!arenaInstance.getRegion().isInside(new PLocatable(player), false)) {
                event.setCancelled(true);

                player.teleport(arenaInstance.getSpectatorSpawnpoint().getBukkitLocation());
                player.sendMessage(ChatColor.RED + "You have been teleported back to the Spectator Spawnpoint");
            }
        });
    }
}
