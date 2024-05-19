package net.hcfrevival.arena.listener;

import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.DuelMatchFinishEvent;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.ranked.RankedManager;
import net.hcfrevival.arena.ranked.impl.RankedPlayer;
import net.hcfrevival.arena.session.impl.RankedDuelSession;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.hcfrevival.arena.util.RatingUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record RankedDataListener(ArenaPlugin plugin) implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }

        RankedManager rankedManager = (RankedManager) plugin.getManagers().get(RankedManager.class);
        RankedPlayer profile = (RankedPlayer) rankedManager.loadProfile(event.getUniqueId());

        if (profile != null) {
            rankedManager.getRankedProfileRepository().add(profile);
            return;
        }

        rankedManager.getRankedProfileRepository().add(new RankedPlayer(event.getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RankedManager rankedManager = (RankedManager) plugin.getManagers().get(RankedManager.class);

        rankedManager.getProfile(event.getPlayer().getUniqueId()).ifPresent(profile -> {
            rankedManager.saveProfile(profile, true);
            rankedManager.getRankedProfileRepository().remove(profile);
        });
    }

    @EventHandler
    public void onDuelMatchFinish(DuelMatchFinishEvent event) {
        if (!(event.getSession() instanceof final RankedDuelSession rankedSession)) {
            return;
        }

        RankedManager rankedManager = (RankedManager) plugin.getManagers().get(RankedManager.class);
        ArenaPlayer winner = event.getWinner();
        ArenaPlayer loser = event.getLoser();
        PlayerStatHolder winnerStats = rankedSession.getStats(winner).orElseThrow(NullPointerException::new);
        PlayerStatHolder loserStats = rankedSession.getStats(loser).orElseThrow(NullPointerException::new);
        RankedPlayer winnerRankedData = (RankedPlayer) rankedManager.getProfile(winner.getUniqueId()).orElse(new RankedPlayer(winner.getUniqueId()));
        RankedPlayer loserRankedData = (RankedPlayer) rankedManager.getProfile(loser.getUniqueId()).orElse(new RankedPlayer(loser.getUniqueId()));

        final int oldWinnerRating = winnerRankedData.getRating(event.getSession().getGamerule());
        final int oldLoserRating = loserRankedData.getRating(event.getSession().getGamerule());

        int[] ratingChanges = RatingUtil.calculateRatingChangeExperimental(
                winnerStats,
                loserStats,
                winnerRankedData.getRating(event.getSession().getGamerule()),
                loserRankedData.getRating(event.getSession().getGamerule()));

        int newWinnerRating = ratingChanges[0];
        int newLoserRating = ratingChanges[1];
        int winnerRatingDiff = (newWinnerRating - oldWinnerRating);
        int loserRatingDiff = (newLoserRating - oldLoserRating);

        winnerRankedData.setRating(event.getSession().getGamerule(), newWinnerRating);
        loserRankedData.setRating(event.getSession().getGamerule(), newLoserRating);

        Component rankChangeComponent = Component.text(winner.getUsername(), NamedTextColor.WHITE).appendSpace().append(Component.text("[" + newWinnerRating + " +" + winnerRatingDiff + "]", NamedTextColor.GREEN))
                .appendSpace().append(Component.text("-", NamedTextColor.GRAY)).appendSpace()
                .append(Component.text(loser.getUsername(), NamedTextColor.WHITE).appendSpace().append(Component.text("[" + newLoserRating + " " + loserRatingDiff + "]", NamedTextColor.RED)));

        Component component = Component.text("Rating Change", NamedTextColor.LIGHT_PURPLE).append(Component.text(":", NamedTextColor.WHITE)).appendSpace();

        // Player A [+33] - Player B [-8]
        winner.getPlayer().ifPresent(p -> p.sendMessage(component.append(rankChangeComponent)));
        loser.getPlayer().ifPresent(p -> p.sendMessage(component.append(rankChangeComponent)));
    }
}
