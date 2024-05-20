package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.Description;
import gg.hcfactions.libs.acf.annotation.Subcommand;
import gg.hcfactions.libs.acf.annotation.Syntax;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.menu.RecapMenu;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@CommandAlias("match|m")
public final class MatchCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

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
