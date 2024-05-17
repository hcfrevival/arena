package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.Subcommand;
import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.team.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("team|t")
public final class TeamCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("inv|invite")
    public void onTeamInvite(Player player, String invitedName) {
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getExecutor().sendInvite(player, invitedName, new Promise() {
            @Override
            public void resolve() {
                player.sendMessage(Component.text("Invite has been sent", NamedTextColor.GREEN));
            }

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text("Failed to send invite: " + s, NamedTextColor.RED));
            }
        });
    }

    @Subcommand("uninv|uninvite")
    public void onTeamUninvite(Player player, String uninvitedName) {
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getExecutor().revokeInvite(player, uninvitedName, new Promise() {
            @Override
            public void resolve() {}

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text("Failed to revoke invite: " + s, NamedTextColor.RED));
            }
        });
    }

    @Subcommand("join")
    public void onTeamJoin(Player player, String teamId) {
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getExecutor().joinTeam(player, teamId, new Promise() {
            @Override
            public void resolve() {}

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text("Failed to join team: " + s, NamedTextColor.RED));
            }
        });
    }
}
