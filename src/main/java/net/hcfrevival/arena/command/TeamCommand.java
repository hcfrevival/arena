package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.Subcommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("team|t")
public final class TeamCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("inv|invite")
    public void onTeamInvite(Player player, Player invited) {

    }

    @Subcommand("uninv|uninvite")
    public void onTeamUninvite(Player player, Player uninvited) {

    }

    @Subcommand("join")
    public void onTeamJoin(Player player, String teamId) {

    }
}
