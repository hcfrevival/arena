package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.kit.impl.DefaultKit;
import net.hcfrevival.arena.kit.impl.PlayerKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
@CommandAlias("akit")
public final class KitCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    /*
        /kit save NO_DEBUFF [-d]
     */

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

        if (isDefault) {
            final DefaultKit defaultKit = new DefaultKit(gamerule, Arrays.asList(player.getInventory().getContents()), Arrays.asList(player.getInventory().getArmorContents()));
            kitManager.saveDefaultKit(defaultKit);
            player.sendMessage(ChatColor.GREEN + "Default kit has been saved");
            return;
        }

        final PlayerKit playerKit = new PlayerKit(player.getUniqueId(), gamerule, Arrays.asList(player.getInventory().getContents()), Arrays.asList(player.getInventory().getArmorContents()));
        kitManager.savePlayerKit(player, playerKit);
        player.sendMessage(ChatColor.GREEN + "Kit saved");
    }
}
