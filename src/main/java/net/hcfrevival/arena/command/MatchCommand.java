package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.menu.MatchMenu;
import net.hcfrevival.arena.menu.RecapMenu;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@CommandAlias("match|m")
public final class MatchCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Subcommand("list")
    @Description("View a list of all active matches")
    public void onMatchList(Player player) {
        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        MatchMenu menu = new MatchMenu(plugin, player, sessionManager.getSessionRepository());
        menu.open();
    }

    @Subcommand("end")
    @Description("Force end a match")
    @CommandPermission(APermissions.A_MOD)
    @CommandCompletion("@players")
    public void onMatchEnd(Player player, String username) {
        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);

        Player otherPlayer = Bukkit.getPlayer(username);
        if (otherPlayer == null) {
            player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }

        sessionManager.getSession(otherPlayer).ifPresentOrElse((session) -> sessionManager.endSession(session, true), () ->
                player.sendMessage(Component.text("Session not found", NamedTextColor.RED)));
    }

    @Subcommand("invsee|inv")
    @Description("View a post-match inventory")
    @Syntax("<match> <username>")
    public void onInventorySee(Player player, String matchId, String username) {
        final UUID matchUid;
        try {
            matchUid = UUID.fromString(matchId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid match UID");
            return;
        }

        final SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);
        final Optional<ISession> sessionQuery = sessionManager.getSessionHistory(matchUid);

        if (sessionQuery.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Session not found");
            return;
        }

        final ISession session = sessionQuery.get();
        final Optional<PlayerStatHolder> statsQuery = session.getFinalStats().stream().filter(stats -> stats.getUsername().equalsIgnoreCase(username)).findFirst();

        if (statsQuery.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        final PlayerStatHolder holder = statsQuery.get();
        final RecapMenu menu = new RecapMenu(plugin, player, session, holder);
        menu.open();
    }
}
