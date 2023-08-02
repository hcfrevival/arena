package net.hcfrevival.arena.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerJoinQueueEvent;
import net.hcfrevival.arena.event.PlayerLeaveQueueEvent;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public final class QueueListener implements Listener {
    @Getter public final ArenaPlugin plugin;

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoinQueue(PlayerJoinQueueEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        player.sendMessage(ArenaMessage.getJoinQueueMessage(event.getQueueEntry()));
        LobbyUtil.giveQueueItems(plugin, player);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerLeaveQueue(PlayerLeaveQueueEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        player.sendMessage(ArenaMessage.getLeaveQueueMessage(event.getQueueEntry()));
        LobbyUtil.giveLobbyItems(plugin, player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final QueueManager queueManager = (QueueManager) plugin.getManagers().get(QueueManager.class);

        queueManager.getQueueRepository().removeIf(queue -> queue.getUniqueId().equals(player.getUniqueId()));
    }
}
