package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.PaginatedMenu;
import net.hcfrevival.arena.level.IArenaInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class ArenaInstanceMenu extends PaginatedMenu<IArenaInstance> {
    public ArenaInstanceMenu(AresPlugin plugin, Player player, String arenaName, int rows, Collection<IArenaInstance> entries) {
        super(plugin, player, arenaName + " Instances", rows, entries);
    }

    @Override
    public List<IArenaInstance> sort() {
        List<IArenaInstance> sorted = Lists.newArrayList(entries);
        sorted.sort(Comparator.comparing(IArenaInstance::isAvailable));
        return sorted;
    }

    @Override
    public Clickable getItem(IArenaInstance instance, int i) {
        ItemStack icon = new ItemBuilder().setMaterial(Material.WRITABLE_BOOK)
                .setName(Component.text("ID", NamedTextColor.GRAY)
                        .append(Component.text(": " + instance.getUniqueId().toString(), NamedTextColor.WHITE)))
                .build();

        return new Clickable(icon, i, click -> player.teleport(instance.getSpectatorSpawnpoint().getBukkitLocation()));
    }
}
