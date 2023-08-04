package net.hcfrevival.arena.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.bukkit.services.impl.items.ICustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.menu.KitSelectMenu;
import net.hcfrevival.arena.queue.QueueManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class RankedQueueItem implements ICustomItem {
    @Getter public final ArenaPlugin plugin;

    @Override
    public Material getMaterial() {
        return Material.NETHERITE_SWORD;
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Join Ranked Queue";
    }

    @Override
    public List<String> getLore() {
        final List<String> res = Lists.newArrayList();
        res.add(ChatColor.GRAY + "Ranked Queue will pair you against");
        res.add(ChatColor.GRAY + "players with a similar skill rating to");
        res.add(ChatColor.GRAY + "your own.");
        return res;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
    }

    @Override
    public ItemStack getItem() {
        final ItemStack item = ICustomItem.super.getItem();
        final ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {
            final KitSelectMenu menu = new KitSelectMenu(plugin, who, new FailablePromise<>() {
                @Override
                public void resolve(EGamerule rule) {
                    final QueueManager queueManager = (QueueManager)plugin.getManagers().get(QueueManager.class);

                    queueManager.getExecutor().addToRankedQueue(who, rule, new Promise() {
                        @Override
                        public void resolve() {
                            who.sendMessage("Joined Ranked " + rule.getDisplayName() + ChatColor.RESET + " Queue");
                        }

                        @Override
                        public void reject(String s) {
                            who.sendMessage(ChatColor.RED + "Failed to join queue: " + s);
                        }
                    });
                }

                @Override
                public void reject(String s) {
                    who.sendMessage(ChatColor.RED + "Failed to select kit: " + s);
                }
            });

            menu.open();
        };
    }
}
