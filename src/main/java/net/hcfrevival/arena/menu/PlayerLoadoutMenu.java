package net.hcfrevival.arena.menu;

import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.GenericMenu;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import net.hcfrevival.classes.types.IClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
public final class PlayerLoadoutMenu extends GenericMenu {
    public final ArenaPlugin plugin;
    public final Team team;
    public final ArenaPlayer editedPlayer;

    public PlayerLoadoutMenu(ArenaPlugin plugin, Player player, Team editedTeam, ArenaPlayer editedPlayer) {
        super(plugin, player, editedPlayer.getUsername() + "'s Loadout", 2);
        this.plugin = plugin;
        this.team = editedTeam;
        this.editedPlayer = editedPlayer;
    }

    private void populate() {
        TeamLoadoutConfig.ELoadoutValue currentValue = team.getLoadoutConfig().getLoadoutValue(editedPlayer.getUniqueId());

        int cursor = 0;
        for (TeamLoadoutConfig.ELoadoutValue value : TeamLoadoutConfig.ELoadoutValue.values()) {
            Optional<IClass> classQuery = TeamLoadoutConfig.ELoadoutValue.getAssociatedClass(plugin, value);
            Material material = Material.NETHERITE_HELMET;
            boolean selected = (currentValue == value);

            if (classQuery.isPresent()) {
                material = classQuery.get().getIcon();
            }

            ItemBuilder builder = new ItemBuilder()
                    .setMaterial(material)
                    .setName(Component.text(Strings.capitalize(value.name().toLowerCase()), NamedTextColor.GOLD));

            if (value.isDisabled()) {
                builder.addLore(Component.text("Disabled", NamedTextColor.RED));
            }

            if (selected) {
                builder.addEnchant(Enchantment.UNBREAKING, 0);
                builder.addFlag(ItemFlag.HIDE_ENCHANTS);
                builder.addLore(Component.text("Selected", NamedTextColor.GREEN));
            }

            builder.addFlag(ItemFlag.HIDE_ATTRIBUTES);
            builder.addFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

            ItemStack item = builder.build();
            addItem(new Clickable(item, cursor, click -> {
                if (selected || value.isDisabled()) {
                    return;
                }

                team.getLoadoutConfig().setLoadoutValue(editedPlayer.getUniqueId(), value);
                populate();
            }));

            cursor++;
        }

        ItemStack backIcon = new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName(Component.text("Back", NamedTextColor.RED))
                .build();

        addItem(new Clickable(backIcon, 8, click -> {
            TeamLoadoutMenu loadoutMenu = new TeamLoadoutMenu(plugin, player, team);
            loadoutMenu.open();
        }));
    }

    @Override
    public void open() {
        super.open();
        populate();
    }
}
