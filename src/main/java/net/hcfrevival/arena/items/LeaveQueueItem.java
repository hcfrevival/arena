package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerLeaveQueueEvent;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.queue.impl.IArenaQueue;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public final class LeaveQueueItem implements ICustomItem {
    @Getter public final ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.BARRIER;
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Leave Queue";
    }

    @Override
    public List<String> getLore() {
        return Lists.newArrayList();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            final QueueManager queueManager = (QueueManager) plugin.getManagers().get(QueueManager.class);
            final Optional<IArenaQueue> queueQuery = queueManager.getQueue(who);

            if (queueQuery.isEmpty()) {
                plugin.getAresLogger().error("attempted to remove " + who.getName() + " from a queue but there was none found");
                return;
            }

            final IArenaQueue queue = queueQuery.get();
            final PlayerLeaveQueueEvent leaveEvent = new PlayerLeaveQueueEvent(who, queue);

            Bukkit.getPluginManager().callEvent(leaveEvent);

            if (leaveEvent.isCancelled()) {
                return;
            }

            queueManager.getQueueRepository().remove(queue);
            LobbyUtil.giveLobbyItems(plugin, who);
        };
    }
}
