package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.bukkit.services.impl.account.AccountService;
import gg.hcfactions.libs.bukkit.services.impl.account.model.AresAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.session.IDuelRequest;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.team.impl.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@CommandAlias("duel")
@AllArgsConstructor
public final class DuelCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Default
    @Description("Duel a player")
    @Syntax("<player>")
    public void onDuelPlayer(Player player, String username) {
        AccountService acs = (AccountService) plugin.getService(AccountService.class);

        Player toDuel = Bukkit.getPlayer(username);
        if (toDuel == null || !toDuel.isOnline()) {
            player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }

        if (toDuel.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("You can not duel yourself", NamedTextColor.RED));
            return;
        }

        if (acs != null) {
            AresAccount senderAresAccount = acs.getCachedAccount(player.getUniqueId());
            AresAccount receiverAresAccount = acs.getCachedAccount(toDuel.getUniqueId());

            if (senderAresAccount != null && receiverAresAccount != null) {
                // Receiver is ignoring the sender
                if (receiverAresAccount.getSettings().isIgnoring(senderAresAccount.getUniqueId())) {
                    player.sendMessage(Component.text(receiverAresAccount.getUsername() + " is not accepting duels", NamedTextColor.RED));
                    return;
                }

                // Sender is ignoring the receiver
                if (senderAresAccount.getSettings().isIgnoring(receiverAresAccount.getUniqueId())) {
                    player.sendMessage(Component.text("You can not duel this player because you are ignoring them", NamedTextColor.RED));
                    return;
                }

                // Receiver has duel requests disabled
                if (!receiverAresAccount.getSettings().isEnabled(AresAccount.Settings.SettingValue.ALLOW_DUEL_REQUESTS)) {
                    player.sendMessage(Component.text(receiverAresAccount.getUsername() + " is not accepting duels", NamedTextColor.RED));
                    return;
                }
            }
        }

        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        sessionManager.getDuelRequestManager().getExecutor().promptDuelKitSelect(player, username, new Promise() {
            @Override
            public void resolve() {}

            @Override
            public void reject(String s) {
                player.sendMessage(Component.text("Failed to send duel request: " + s, NamedTextColor.RED));
            }
        });
    }

    @Subcommand("accept")
    @Description("Accept a pending duel request")
    @Syntax("<id>")
    public void onDuelAccept(Player player, String reqId) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(reqId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid request ID", NamedTextColor.RED));
            return;
        }

        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
        Optional<Team> teamQuery = teamManager.getTeam(player.getUniqueId());

        if (teamQuery.isEmpty()) {
            player.sendMessage(Component.text("Team not found", NamedTextColor.RED));
            return;
        }

        Team team = teamQuery.get();
        if (!team.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("You are not the team leader", NamedTextColor.RED));
            return;
        }

        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        sessionManager.getDuelRequestManager().getRequest(uuid).ifPresentOrElse(IDuelRequest::accept, () -> player.sendMessage(Component.text("Request not found", NamedTextColor.RED)));
    }
}
