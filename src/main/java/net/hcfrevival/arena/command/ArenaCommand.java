package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.CommandHelp;
import gg.hcfactions.libs.acf.annotation.*;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.level.builder.ERegionCorner;
import net.hcfrevival.arena.level.builder.impl.GenericLevelBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

@AllArgsConstructor
@CommandAlias("arena")
public final class ArenaCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("create")
    @Description("Add a new Arena to the server. This arena acts as a container for Arena Instances.")
    @CommandPermission(APermissions.A_ADMIN)
    @Syntax("<name>")
    public void onArenaCreate(Player player, String arenaName, @Optional String isTeam) {
        boolean flag = (isTeam != null && isTeam.equalsIgnoreCase("-t"));
        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        levelManager.getLevelBuilderManager().getExecutor().createNewLevelContainer(player, arenaName, flag, new Promise() {
            @Override
            public void resolve() {
                player.sendMessage(Component.text("Arena created"));
            }

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text("Failed to create arena: " + s, NamedTextColor.RED));
            }
        });
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
        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        levelManager.getLevelBuilderManager().getExecutor().startBuildingDuelLevel(player, arenaName, new Promise() {
            @Override
            public void resolve() {}

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text(s, NamedTextColor.RED));
            }
        });
    }

    @Subcommand("build setpos")
    @Description("Set a position in the Level Builder")
    @CommandPermission(APermissions.A_ADMIN)
    public void onArenaPositionSet(Player player) {
        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        levelManager.getLevelBuilderManager().getBuilder(player).ifPresentOrElse(builder -> {
            if (builder instanceof final GenericLevelBuilder gb) {
                if (gb.getBuildStage().equals(GenericLevelBuilder.EBuildStage.SPECTATOR_SPAWN)) {
                    gb.setSpectatorSpawnPosition(new PLocatable(player));
                    return;
                }

                else if (gb.getBuildStage().equals(GenericLevelBuilder.EBuildStage.PLAYER_SPAWN)) {
                    gb.addSpawnPosition(new PLocatable(player));
                    return;
                }

                player.sendMessage(Component.text("You are not actively setting a position", NamedTextColor.RED));
                return;
            }

            player.sendMessage(Component.text("Unsupported level builder type", NamedTextColor.RED));
        }, () -> player.sendMessage(Component.text("You are not building an arena", NamedTextColor.RED)));
    }

    @Subcommand("build setpos done")
    @Description("Finalize spawn positions in the Level Builder")
    @CommandPermission(APermissions.A_ADMIN)
    public void onFinalizePositions(Player player) {
        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        levelManager.getLevelBuilderManager().getBuilder(player).ifPresentOrElse(builder -> {
            if (builder instanceof final GenericLevelBuilder gb) {
                if (!gb.getBuildStage().equals(GenericLevelBuilder.EBuildStage.PLAYER_SPAWN)) {
                    player.sendMessage(Component.text("You are not actively setting a position", NamedTextColor.RED));
                    return;
                }

                gb.finalizePlayerSpawnPositions();
                return;
            }

            player.sendMessage(Component.text("Unsupported level builder type", NamedTextColor.RED));
        }, () -> player.sendMessage(Component.text("You are not building an arena", NamedTextColor.RED)));
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
