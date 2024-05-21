package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.PaginatedMenu;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.level.IArenaInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public final class ArenaMenu extends PaginatedMenu<IArena> {
    public ArenaMenu(AresPlugin plugin, Player player, Collection<IArena> entries) {
        super(plugin, player, "Arenas", 6, entries);
    }

    @Override
    public List<IArena> sort() {
        List<IArena> sorted = Lists.newArrayList(entries);
        sorted.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return sorted;
    }

    @Override
    public Clickable getItem(IArena arena, int i) {
        List<Component> loreComponents = Lists.newArrayList();
        loreComponents.add(Component.text("Internal Name", NamedTextColor.GRAY).append(Component.text(": " + arena.getName(), NamedTextColor.WHITE)));
        loreComponents.add(Component.text("Available Instances", NamedTextColor.GRAY).append(Component.text(": " + arena.getInstances().stream().filter(IArenaInstance::isAvailable).count(), NamedTextColor.WHITE)));
        loreComponents.add(Component.text("Total Instances", NamedTextColor.GRAY).append(Component.text(": " + arena.getInstances().size(), NamedTextColor.WHITE)));

        ItemStack icon = new ItemBuilder()
                .setMaterial(Material.WRITABLE_BOOK)
                .setName(arena.getDisplayName())
                .addLoreComponents(loreComponents)
                .build();

        return new Clickable(icon, i, click -> {
            ArenaInstanceMenu instanceMenu = new ArenaInstanceMenu(plugin, player, arena.getName(), 6, arena.getInstances());
            player.closeInventory();
            new Scheduler(plugin).sync(instanceMenu::open).delay(1L).run();
        });
    }
}
