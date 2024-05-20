package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.CommandPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.menu.KitEditorMenu;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class DebugCommand extends BaseCommand {
    @Getter public ArenaPlugin plugin;

    @CommandAlias("debugmode")
    @CommandPermission(APermissions.A_ADMIN)
    public void onDebugmode(Player player) {
        KitEditorMenu menu = new KitEditorMenu(plugin, player, EGamerule.NODEBUFF);
        menu.open();
    }
}
