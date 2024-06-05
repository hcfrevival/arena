package net.hcfrevival.arena.kit;

import net.hcfrevival.arena.ArenaMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IArenaKit {
    String getName();
    List<ItemStack> getContents();

    default void apply(Player player, boolean message) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        int cursor = 0;
        for (ItemStack item : getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                player.getInventory().setItem(cursor, item);
            }

            cursor += 1;
        }

        if (message) {
            player.sendMessage(ArenaMessage.getKitAppliedMessage(getName()));
        }
    }
}
