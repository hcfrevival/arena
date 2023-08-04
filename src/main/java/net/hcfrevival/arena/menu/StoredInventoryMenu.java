package net.hcfrevival.arena.menu;

import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import lombok.Getter;
import net.hcfrevival.arena.inventory.StoredInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class StoredInventoryMenu extends GenericMenu {
    @Getter public final StoredInventory storedInventory;

    public StoredInventoryMenu(AresPlugin plugin, Player player, StoredInventory storedInventory) {
        super(plugin, player, storedInventory.getUsername(), 6);
        this.storedInventory = storedInventory;
    }

    @Override
    public void open() {
        super.open();

        int cursor = 0;

        for (ItemStack item : storedInventory.getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                cursor += 1;
                continue;
            }

            addItem(new Clickable(item, cursor, click -> {}));
            cursor += 1;
        }
    }
}
