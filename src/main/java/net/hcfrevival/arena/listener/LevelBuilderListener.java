package net.hcfrevival.arena.listener;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.level.builder.ERegionCorner;
import net.hcfrevival.arena.level.builder.impl.GenericLevelBuilder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.UUID;

public final class LevelBuilderListener implements Listener {
    @Getter public final ArenaPlugin plugin;
    private final Set<UUID> interactCooldowns;

    public LevelBuilderListener(ArenaPlugin plugin) {
        this.plugin = plugin;
        this.interactCooldowns = Sets.newConcurrentHashSet();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || block.getType().isAir()) {
            return;
        }

        if (interactCooldowns.contains(player.getUniqueId())) {
            return;
        }

        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);

        levelManager.getLevelBuilderManager().getBuilder(player).ifPresent(levelBuilder -> {
            if (levelBuilder instanceof final GenericLevelBuilder gb) {
                if (gb.getBuildStage().equals(GenericLevelBuilder.EBuildStage.CORNER_A)) {
                    gb.setArenaRegionCorner(new BLocatable(block), ERegionCorner.A);
                }

                else if (gb.getBuildStage().equals(GenericLevelBuilder.EBuildStage.CORNER_B)) {
                    gb.setArenaRegionCorner(new BLocatable(block), ERegionCorner.B);
                }
            }

            interactCooldowns.add(player.getUniqueId());
            new Scheduler(plugin).sync(() -> interactCooldowns.remove(player.getUniqueId())).delay(20L).run();
        });
    }
}
