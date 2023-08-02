package net.hcfrevival.arena.menu;

import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import net.hcfrevival.arena.gamerule.EGamerule;
import org.bukkit.entity.Player;

public final class KitSelectMenu extends GenericMenu {
    private final FailablePromise<EGamerule> callback;

    public KitSelectMenu(AresPlugin plugin, Player player, FailablePromise<EGamerule> callback) {
        super(plugin, player, "Kit Select", 1);
        this.callback = callback;
    }

    @Override
    public void open() {
        super.open();

        int cursor = 0;

        for (EGamerule rule : EGamerule.values()) {
            addItem(new Clickable(EGamerule.getIcon(rule), cursor, click -> {
                callback.resolve(rule);
                player.closeInventory();
            }));

            cursor += 1;
        }
    }
}
