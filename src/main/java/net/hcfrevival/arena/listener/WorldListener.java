package net.hcfrevival.arena.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public final class WorldListener implements Listener {
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}
