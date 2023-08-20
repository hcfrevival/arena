package net.hcfrevival.arena.team;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.arena.util.LobbyUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class TeamManager extends ArenaManager {
    @Getter public final Set<Team> teamRepository;

    public TeamManager(ArenaPlugin plugin) {
        super(plugin);
        this.teamRepository = Sets.newConcurrentHashSet();
    }

    public Optional<Team> getTeam(UUID uid) {
        return teamRepository.stream().filter(t -> t.getUniqueId().equals(uid)).findFirst();
    }

    public Optional<Team> getTeam(Player player) {
        return teamRepository.stream().filter(t -> t.isMember(player.getUniqueId())).findFirst();
    }

    public Optional<Team> getTeam(ArenaPlayer arenaPlayer) {
        return teamRepository.stream().filter(t -> t.isMember(arenaPlayer.getUniqueId())).findFirst();
    }

    public void createTeam(ArenaPlayer leader) {
        if (getTeam(leader).isPresent()) {
            leader.getPlayer().ifPresent(player -> player.sendMessage(ChatColor.RED + "You are already in a team"));
            return;
        }

        final Team team = new Team(leader);
        teamRepository.add(team);

        leader.setCurrentState(EPlayerState.LOBBY_IN_PARTY);
        leader.getPlayer().ifPresent(player -> LobbyUtil.givePartyLeaderItems(plugin, player));
        leader.getPlayer().ifPresent(player -> player.sendMessage(ChatColor.GREEN + "Team created"));
    }

    public void disbandTeam(Team team) {
        team.sendMessage(ChatColor.YELLOW + "Team disbanded");

        team.getFullMembers().forEach(teamMember -> {
            if (teamMember.getCurrentState().equals(EPlayerState.LOBBY_IN_PARTY)) {
                teamMember.setCurrentState(EPlayerState.LOBBY);
            }

            teamMember.clearFriendlies();
            teamMember.getPlayer().ifPresent(player -> LobbyUtil.giveLobbyItems(plugin, player));
        });

        teamRepository.remove(team);
    }
}
