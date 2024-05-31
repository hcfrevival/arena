package net.hcfrevival.arena.menu;

import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.queue.QueueManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class KitSelectMenu extends GenericMenu {
    private final ArenaPlugin plugin;
    private final FailablePromise<EGamerule> callback;
    private final EQueueDataType queueDataType;

    public KitSelectMenu(ArenaPlugin plugin, Player player, FailablePromise<EGamerule> callback) {
        super(plugin, player, "Kit Select", 1);
        this.plugin = plugin;
        this.callback = callback;
        this.queueDataType = null;
    }

    public KitSelectMenu(ArenaPlugin plugin, Player player, EQueueDataType queueDataType, FailablePromise<EGamerule> callback) {
        super(plugin, player, "Kit Select", 1);
        this.plugin = plugin;
        this.callback = callback;
        this.queueDataType = queueDataType;
    }

    @Override
    public void open() {
        super.open();

        if (queueDataType != null) {
            addUpdater(this::populate, 100L);
            return;
        }

        populate();
    }

    private void populate() {
        int cursor = 0;

        for (EGamerule rule : EGamerule.values()) {
            ItemStack icon = EGamerule.getIcon(rule);

            if (queueDataType != null) {
                QueueManager queueManager = (QueueManager) plugin.getManagers().get(QueueManager.class);
                int inQueue = (queueDataType.equals(EQueueDataType.UNRANKED) ? queueManager.getUnrankedQueues(rule).size() : queueManager.getRankedQueues(rule).size());
                icon = EGamerule.getIcon(rule, inQueue);
            }

            addItem(new Clickable(icon, cursor, click -> {
                callback.resolve(rule);
                player.closeInventory();
            }));

            cursor += 1;
        }
    }

    public enum EQueueDataType {
        UNRANKED, RANKED
    }
}
