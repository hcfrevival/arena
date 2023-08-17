package net.hcfrevival.arena.kit;

import net.hcfrevival.arena.ArenaMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IArenaKit {
    String getName();
    List<ItemStack> getContents();
    List<ItemStack> getArmorContents();

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

        ItemStack[] armorContents = new ItemStack[getArmorContents().size()];
        armorContents = getArmorContents().toArray(armorContents);

        player.getInventory().setArmorContents(armorContents);

        if (message) {
            player.sendMessage(ArenaMessage.getKitAppliedMessage(getName()));
        }
    }
}
