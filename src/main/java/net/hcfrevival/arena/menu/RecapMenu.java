package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import lombok.Getter;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class RecapMenu extends GenericMenu {
    @Getter public final ISession session;
    @Getter public final PlayerStatHolder holder;

    public RecapMenu(AresPlugin plugin, Player player, ISession session, PlayerStatHolder holder) {
        super(plugin, player, holder.getUsername(), 6);
        this.session = session;
        this.holder = holder;
    }

    @Override
    public void open() {
        for (int i = 0; i < holder.getContents().size(); i++) {
            addItem(new Clickable(holder.getContents().get(i), i, click -> {}));
        }

        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        final ItemMeta meta = skull.getItemMeta();

        if (meta != null) {
            final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            final List<String> lore = Lists.newArrayList();

            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(holder.getOwner()));
            skullMeta.setDisplayName(ChatColor.AQUA + holder.getUsername());

            lore.add(ChatColor.GOLD + "W-Tap" + ChatColor.YELLOW + ": " + holder.getSprintResetAccuracy() + "%");
            lore.add(ChatColor.GOLD + "Total Hits" + ChatColor.YELLOW + ": " + holder.getTotalHits());
            lore.add(ChatColor.GOLD + "Total Damage" + ChatColor.YELLOW + ": " + String.format("%,d", holder.getTotalDamage()));
            lore.add(ChatColor.GOLD + "Biggest Combo" + ChatColor.YELLOW + ": " + holder.getBiggestCombo());
            lore.add(ChatColor.GOLD + "Potion Accuracy" + ChatColor.YELLOW + ": " + (int)Math.round(holder.getPotionAccuracy() * 100) + "%");

            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);

            addItem(new Clickable(skull, 49, click -> {}));
        }

        ItemStack healthItem = new ItemBuilder()
                .setMaterial(Material.GLISTERING_MELON_SLICE)
                .setName(Component.text("Health", NamedTextColor.RED))
                .addLore(Component.text(String.format("%.1f", (holder.getHealth() / 2)) + " ♥"))
                .build();

        ItemStack foodItem = new ItemBuilder()
                .setMaterial(Material.COOKED_BEEF)
                .setName(Component.text("Food", NamedTextColor.GOLD))
                .addLore(Component.text(holder.getFoodLevel() / 2) + "/10")
                .build();

        addItem(new Clickable(healthItem, 52, click -> {}));
        addItem(new Clickable(foodItem, 53, click -> {}));

        super.open();
    }
}
