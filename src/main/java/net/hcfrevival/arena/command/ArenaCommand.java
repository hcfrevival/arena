package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.CommandHelp;
import gg.hcfactions.libs.acf.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("arena")
public final class ArenaCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("create")
    @Description("Add a new Arena to the server. This arena acts as a container for Arena Instances.")
    @CommandPermission(APermissions.A_ADMIN)
    @Syntax("<name>")
    public void onArenaCreate(Player player, String arenaName) {

    }

    @Subcommand("del|delete")
    @Description("Delete an existing Arena, and all of it's instances from the server.")
    @CommandPermission(APermissions.A_ADMIN)
    @Syntax("<name>")
    @CommandCompletion("@arenas")
    public void onArenaDelete(Player player, String arenaName) {

    }

    @Subcommand("add")
    @Description("Enter the editor and create a new Arena Instance for the provided Arena Name.")
    @CommandPermission(APermissions.A_ADMIN)
    @Syntax("<name>")
    @CommandCompletion("@arenas")
    public void onArenaAdd(Player player, String arenaName) {

    }

    @Subcommand("rem|remove")
    @Description("Remove the Arena Instance you are current located inside of from the server.")
    @CommandPermission(APermissions.A_ADMIN)
    public void onArenaRemove(Player player) {

    }

    @Subcommand("list")
    @Description("Open a GUI with a detailed list of each Arena on the server.")
    @CommandPermission(APermissions.A_MOD)
    @Syntax("[page]")
    public void onArenaList(Player player, @Optional int page) {

    }

    @Subcommand("info")
    @Description("View detailed information about the Arena Instance you are standing inside")
    @CommandPermission(APermissions.A_MOD)
    public void onArenaInfo(Player player) {

    }

    @HelpCommand
    public void onShowHelp(CommandSender sender, CommandHelp help) {

    }
}
