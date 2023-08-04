package net.hcfrevival.arena.session;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.level.impl.TeamArenaInstance;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.RankedDuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.team.Team;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * @param playerA Player A
     * @param playerB Player B
     * @return Optional of RankedDuelSession
     */
    public Optional<RankedDuelSession> createRankedDuelSession(ArenaPlayer playerA, ArenaPlayer playerB) {
        final Optional<DuelSession> newSession = createDuelSession(playerA, playerB, true);

        if (newSession.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((RankedDuelSession) newSession.get());
    }

    /**
     * Create a new DuelSession instance
     * @param playerA Player A
     * @param playerB Player B
     * @param isRanked If true, the returned item will be a RankedDuelSession instance (see createRankedDuelSession)
     * @return Optional of DuelSession
     */
    public Optional<DuelSession> createDuelSession(ArenaPlayer playerA, ArenaPlayer playerB, boolean isRanked) {
        final LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        final Optional<DuelArenaInstance> instQuery = levelManager.getAvailableDuelInstance();

        if (instQuery.isEmpty()) {
            return Optional.empty();
        }

        final DuelArenaInstance instance = instQuery.get();

        if (isRanked) {
            return Optional.of(new RankedDuelSession(instance, playerA, playerB));
        }

        return Optional.of(new DuelSession(instance, playerA, playerB));
    }

    /**
     * Create a new TeamSession instance
     * @param teams Array of Teams involved
     * @return Optional of Team Session
     */
    public Optional<TeamSession> createTeamSession(List<Team> teams) {
        final LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        final Optional<TeamArenaInstance> instQuery = levelManager.getAvailableTeamInstance();

        if (instQuery.isEmpty()) {
            return Optional.empty();
        }

        final TeamArenaInstance instance = instQuery.get();
        final TeamSession session = new TeamSession(instance, teams);

        return Optional.of(session);
    }
}
