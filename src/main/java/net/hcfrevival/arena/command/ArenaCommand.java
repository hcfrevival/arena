package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.CommandHelp;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.HelpCommand;
import gg.hcfactions.libs.acf.annotation.Optional;
import gg.hcfactions.libs.acf.annotation.Subcommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("arena")
public final class ArenaCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("list")
    public void onArenaList(Player player, @Optional int page) {

    }

    @HelpCommand
    public void onShowHelp(CommandSender sender, CommandHelp help) {

    }
}
