package net.hcfrevival.arena.util;

import net.hcfrevival.arena.level.IArenaInstance;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class ArenaUtil {
    public static void clearEntities(IArenaInstance instance) {
        World world = instance.getRegion().getCornerA().getBukkitBlock().getWorld();
        BoundingBox bbox = new BoundingBox(
                instance.getRegion().getCornerA().getX(),
                instance.getRegion().getCornerA().getY(),
                instance.getRegion().getCornerA().getZ(),
                instance.getRegion().getCornerB().getX(),
                instance.getRegion().getCornerB().getY(),
                instance.getRegion().getCornerB().getZ());

        for (Entity entity : world.getNearbyEntities(bbox)) {
            if (entity instanceof Player) {
                continue;
            }

            entity.remove();
        }
    }
}
