package net.hcfrevival.arena.session;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaMessage;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.DuelMatchFinishEvent;
import net.hcfrevival.arena.event.TeamMatchFinishEvent;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.level.impl.TeamArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.RankedDuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.hcfrevival.arena.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SessionManager extends ArenaManager {
    @Getter public final Set<ISession> sessionRepository;

    public SessionManager(ArenaPlugin plugin) {
        super(plugin);
        this.sessionRepository = Sets.newConcurrentHashSet();
    }

    /**
     * Query a Session instance the provided player
     * is involved with.
     *
     * @param player Player
     * @return
     */
    public Optional<ISession> getSession(Player player) {
        for (ISession session : sessionRepository) {
            if (session.isSpectating(player.getUniqueId())) {
                return Optional.of(session);
            }

            if (session instanceof final DuelSession duelSession) {
                if (duelSession.getPlayerA().getUniqueId().equals(player.getUniqueId()) || duelSession.getPlayerB().getUniqueId().equals(player.getUniqueId())) {
                    return Optional.of(session);
                }
            }

            else if (session instanceof final TeamSession teamSession) {
                if (teamSession.getTeams().stream().anyMatch(team -> team.isMember(player.getUniqueId()))) {
                    return Optional.of(session);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Create a new RankedDuelSession instance
     * @param gamerule Game rule set
     * @param playerA Player A
     * @param playerB Player B
     * @return Optional of RankedDuelSession
     */
    public Optional<RankedDuelSession> createRankedDuelSession(EGamerule gamerule, ArenaPlayer playerA, ArenaPlayer playerB) {
        final Optional<DuelSession> newSession = createDuelSession(gamerule, playerA, playerB, true);

        if (newSession.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((RankedDuelSession) newSession.get());
    }

    /**
     * Create a new DuelSession instance
     * @param gamerule Game rule set
     * @param playerA Player A
     * @param playerB Player B
     * @param isRanked If true, the returned item will be a RankedDuelSession instance (see createRankedDuelSession)
     * @return Optional of DuelSession
     */
    public Optional<DuelSession> createDuelSession(EGamerule gamerule, ArenaPlayer playerA, ArenaPlayer playerB, boolean isRanked) {
        final LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        final Optional<DuelArenaInstance> instQuery = levelManager.getAvailableDuelInstance();

        if (instQuery.isEmpty()) {
            return Optional.empty();
        }

        final DuelArenaInstance instance = instQuery.get();

        if (isRanked) {
            return Optional.of(new RankedDuelSession(gamerule, instance, playerA, playerB));
        }

        return Optional.of(new DuelSession(gamerule, instance, playerA, playerB));
    }

    /**
     * Create a new TeamSession instance
     * @param gamerule Game rule set
     * @param teams Array of Teams involved
     * @return Optional of Team Session
     */
    public Optional<TeamSession> createTeamSession(EGamerule gamerule, List<Team> teams) {
        final LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        final Optional<TeamArenaInstance> instQuery = levelManager.getAvailableTeamInstance();

        if (instQuery.isEmpty()) {
            return Optional.empty();
        }

        final TeamArenaInstance instance = instQuery.get();
        final TeamSession session = new TeamSession(gamerule, instance, teams);

        return Optional.of(session);
    }

    public void startSession(ISession session) {
        final KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);

        session.getArena().setAvailable(false);
        session.setStartTimestamp(Time.now());
        session.teleportAll();

        session.getPlayers().stream().filter(arenaPlayer -> !session.isSpectating(arenaPlayer.getUniqueId())).forEach(ingamePlayer -> {
            ingamePlayer.setCurrentState(EPlayerState.INGAME);
            ingamePlayer.setStatHolder(new PlayerStatHolder(ingamePlayer.getUniqueId()));
            ingamePlayer.getPlayer().ifPresent(bukkitPlayer -> kitManager.giveKitBooks(bukkitPlayer, session.getGamerule()));
        });

        sessionRepository.add(session);

        // Countdown
        for (int i = 0; i < 4; i++) {
            final int count = (3 - i);

            new Scheduler(plugin).sync(() -> {
                if (count > 0) {
                    session.sendTitle(ChatColor.GOLD + "Match Starting", ChatColor.YELLOW + "" + count + " second" + (count > 1 ? "s" : ""), 0, 30, 0);
                    session.sendSound(Sound.BLOCK_NOTE_BLOCK_HARP);
                    return;
                }

                session.sendTitle(ChatColor.GREEN + "Fight!", ChatColor.RESET + "", 0, 20, 0);
                session.sendSound(Sound.BLOCK_NOTE_BLOCK_BANJO);
                session.setActive(true);
            }).delay(i * 20L).run();
        }

        new Scheduler(plugin).sync(() ->
                session.sendMessage(ArenaMessage.getArenaDetailMessage(session.getArena().getOwner()))).delay(10 * 20L).run();
    }

    public void endSession(ISession session) {
        session.setEndTimestamp(Time.now());
        session.setActive(false);

        new Scheduler(plugin).sync(() -> session.getPlayers().forEach(player -> {
            player.setCurrentState(EPlayerState.LOBBY);

            Bukkit.broadcastMessage(player.getStatHolder().toString());
            player.setStatHolder(null);
        })).delay(3 * 20L).run();

        new Scheduler(plugin).sync(() -> session.getArena().setAvailable(true)).delay(5 * 20L).run();

        if (session instanceof final DuelSession duelSession) {
            final Optional<ArenaPlayer> winnerQuery = duelSession.getWinner();
            final Optional<ArenaPlayer> loserQuery = duelSession.getLoser();

            if (winnerQuery.isEmpty() || loserQuery.isEmpty()) {
                duelSession.sendMessage(ChatColor.RED + "Failed to perform account lookup while generating after-match report.");
                return;
            }

            final ArenaPlayer winner = winnerQuery.get();
            final ArenaPlayer loser = loserQuery.get();
            final DuelMatchFinishEvent finishEvent = new DuelMatchFinishEvent(duelSession, winner, loser);

            Bukkit.getPluginManager().callEvent(finishEvent);

            sessionRepository.remove(session);

            return;
        }

        if (session instanceof final TeamSession teamSession) {
            final Optional<Team> winnerQuery = teamSession.getWinner();

            if (winnerQuery.isEmpty()) {
                teamSession.sendMessage(ChatColor.RED + "Failed to perform winner lookup while generating after-match report.");
                return;
            }

            final Team winner = winnerQuery.get();
            final List<Team> losers = teamSession.getTeams().stream().filter(t -> !t.getUniqueId().equals(winner.getUniqueId())).collect(Collectors.toList());
            final TeamMatchFinishEvent finishEvent = new TeamMatchFinishEvent(teamSession, winner, losers);

            Bukkit.getPluginManager().callEvent(finishEvent);
        }
    }
}
