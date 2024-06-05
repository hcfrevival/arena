package net.hcfrevival.arena.command;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import gg.hcfactions.libs.bukkit.utils.Items;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.kit.impl.DefaultKit;
import net.hcfrevival.arena.kit.impl.HCFDefaultKit;
import net.hcfrevival.arena.kit.impl.HCFPlayerKit;
import net.hcfrevival.arena.kit.impl.PlayerKit;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@CommandAlias("akit")
public final class KitCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("load")
    @Description("Load a kit")
    @Syntax("<gamerule> [-d]")
    @CommandPermission(APermissions.A_ADMIN)
    public void onKitLoad(Player player, String gameruleName, @Optional String defaultFlag) {
        final boolean isDefault = (defaultFlag != null && defaultFlag.equalsIgnoreCase("-d"));
        final EGamerule gamerule;
        try {
            gamerule = EGamerule.valueOf(gameruleName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid gamerule");
            return;
        }

        final KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);

        if (isDefault) {
            final java.util.Optional<DefaultKit> defaultKitQuery = kitManager.getDefaultKit(gamerule);

            if (defaultKitQuery.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Kit not found");
                return;
            }

            defaultKitQuery.get().apply(player, true);
            return;
        }

        final java.util.Optional<PlayerKit> playerKitQuery = kitManager.getPlayerKit(player, gamerule);

        if (playerKitQuery.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Kit not found");
            return;
        }

        playerKitQuery.get().apply(player, true);
    }

    @Subcommand("save")
    @Description("Save your current inventory as a Kit")
    @Syntax("<gamerule> [-d]")
    @CommandPermission(APermissions.A_ADMIN)
    public void onKitSave(Player player, String gameruleName, @Optional String defaultFlag) {
        final boolean isDefault = (defaultFlag != null && defaultFlag.equalsIgnoreCase("-d"));
        final EGamerule gamerule;
        try {
            gamerule = EGamerule.valueOf(gameruleName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid gamerule");
            return;
        }

        final KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);
        List<ItemStack> contents = Lists.newArrayList();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                contents.add(null);
                continue;
            }

            contents.add(new ItemStack(item));
        }

        if (isDefault) {
            final DefaultKit defaultKit = new DefaultKit(gamerule, contents);
            kitManager.saveDefaultKit(defaultKit);
            player.sendMessage(ChatColor.GREEN + "Default kit has been saved");
            return;
        }

        final PlayerKit playerKit = new PlayerKit(player.getUniqueId(), gamerule, contents);
        kitManager.savePlayerKit(player, playerKit);
        player.sendMessage(ChatColor.GREEN + "Kit saved");
    }

    @Subcommand("hcf load")
    @Description("Save an HCF Kit")
    @Syntax("<class> [-d]")
    @CommandPermission(APermissions.A_ADMIN)
    public void onLoadHCF(Player player, String className, @Optional String defaultFlag) {
        final boolean isDefault = (defaultFlag != null && defaultFlag.equalsIgnoreCase("-d"));
        final TeamLoadoutConfig.ELoadoutValue value;
        try {
            value = TeamLoadoutConfig.ELoadoutValue.valueOf(className);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid class name"));
            return;
        }

        KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);

        if (isDefault) {
            final java.util.Optional<HCFDefaultKit> defaultKitQuery = kitManager.getDefaultKit(value);

            if (defaultKitQuery.isEmpty()) {
                player.sendMessage(Component.text("Kit not found", NamedTextColor.RED));
                return;
            }

            defaultKitQuery.get().apply(player, true);
            return;
        }

        final java.util.Optional<HCFPlayerKit> playerKitQuery = kitManager.getPlayerKit(player, value);

        if (playerKitQuery.isEmpty()) {
            player.sendMessage(Component.text("Kit not found", NamedTextColor.RED));
            return;
        }

        playerKitQuery.get().apply(player, true);
    }

    @Subcommand("hcf save")
    @Description("Save an HCF Kit")
    @Syntax("<class> [-d]")
    @CommandPermission(APermissions.A_ADMIN)
    public void onSaveHCF(Player player, String className, @Optional String defaultFlag) {
        final boolean isDefault = (defaultFlag != null && defaultFlag.equalsIgnoreCase("-d"));
        final TeamLoadoutConfig.ELoadoutValue value;
        try {
            value = TeamLoadoutConfig.ELoadoutValue.valueOf(className);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid class name"));
            return;
        }

        KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);
        List<ItemStack> contents = Lists.newArrayList();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                contents.add(null);
                continue;
            }

            contents.add(new ItemStack(item));
        }

        if (isDefault) {
            HCFDefaultKit kit = new HCFDefaultKit(value, contents);
            kitManager.saveDefaultKit(kit);
            player.sendMessage(Component.text("Default kit has been saved", NamedTextColor.GREEN));
            return;
        }

        HCFPlayerKit kit = new HCFPlayerKit(value, player.getUniqueId(), contents);
        kitManager.savePlayerKit(player, kit);
        player.sendMessage(Component.text("Kit saved", NamedTextColor.GREEN));
    }
}
