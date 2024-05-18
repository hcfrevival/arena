package net.hcfrevival.arena.team;

import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.arena.util.LobbyUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class TeamExecutor {
    @Getter public final TeamManager manager;

    public void createTeam(Player player, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (manager.getTeam(player).isPresent()) {
                promise.reject("You are already on a team");
                return;
            }

            LobbyUtil.givePartyLeaderItems(manager.getPlugin(), player);
            arenaPlayer.setCurrentState(EPlayerState.LOBBY_IN_PARTY);

            Team team = new Team(arenaPlayer);
            manager.getTeamRepository().add(team);

            promise.resolve();
        }, () -> promise.reject("Failed to load your profile"));
    }

    public void disbandTeam(Player player, Promise promise) {
        manager.getTeam(player).ifPresentOrElse(team -> {
            team.sendMessage(Component.text("Team Disbanded", NamedTextColor.YELLOW));

            team.getFullMembers().forEach(teamMember -> {
                if (teamMember.getCurrentState().equals(EPlayerState.LOBBY_IN_PARTY) || teamMember.getCurrentState().equals(EPlayerState.LOBBY_IN_QUEUE)) {
                    teamMember.setCurrentState(EPlayerState.LOBBY);
                }

                teamMember.clearFriendlies();
                teamMember.getPlayer().ifPresent(teamPlayer -> LobbyUtil.giveLobbyItems(manager.getPlugin(), teamPlayer));
            });

            manager.getTeamRepository().remove(team);
        }, () -> promise.reject("You are not on a team"));
    }

    public void leaveTeam(Player player, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);

        manager.getTeam(player).ifPresentOrElse(team -> playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY_IN_QUEUE) && !arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY_IN_PARTY)) {
                promise.reject("You are not in the lobby");
                return;
            }

            team.sendMessage(Component.text(player.getName(), NamedTextColor.AQUA).appendSpace().append(Component.text("has left the team", NamedTextColor.GRAY)));
            team.removeMember(arenaPlayer);

            arenaPlayer.clearFriendlies();
            arenaPlayer.setCurrentState(EPlayerState.LOBBY);
            LobbyUtil.giveLobbyItems(manager.getPlugin(), player);

            promise.resolve();
        }, () -> promise.reject("Failed to obtain your Arena profile")), () -> promise.reject("You are not on a team"));
    }

    public void joinTeam(Player player, String teamName, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        SessionManager sessionManager = (SessionManager) manager.getPlugin().getManagers().get(SessionManager.class);
        boolean bypass = (player.hasPermission(APermissions.A_MOD) || player.hasPermission(APermissions.A_ADMIN));

        manager.getTeam(teamName).ifPresentOrElse(team -> {
            if (!team.isInvited(player.getUniqueId()) && !bypass) {
                promise.reject("You have not been invited to join this team");
                return;
            }

            playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
                boolean giveLobbyItems = true;

                if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                    promise.reject("You are not in the lobby");
                    return;
                }

                Optional<ISession> activeSessionQuery = sessionManager.getSession(team);
                if (activeSessionQuery.isPresent()) {
                    giveLobbyItems = false;
                    activeSessionQuery.get().startSpectating(arenaPlayer);
                }

                team.getInvitedMembers().remove(arenaPlayer.getUniqueId());
                team.addMember(arenaPlayer);
                team.sendMessage(Component.text(player.getName(), NamedTextColor.AQUA).appendSpace().append(Component.text("has joined the team", NamedTextColor.GRAY)));

                if (giveLobbyItems) {
                    LobbyUtil.givePartyMemberItems(manager.getPlugin(), player);
                }

                promise.resolve();
            }, () -> promise.reject("Failed to obtain your Arena profile"));
        }, () -> promise.reject("Team not found"));
    }

    public void sendInvite(Player player, String username, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        Player invited = Bukkit.getPlayer(username);

        if (invited == null || !invited.isOnline()) {
            promise.reject("Player not found");
            return;
        }

        if (invited.getUniqueId().equals(player.getUniqueId())) {
            promise.reject("You can not invite yourself");
            return;
        }

        manager.getTeam(player).ifPresentOrElse(team -> playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!team.getLeader().getUniqueId().equals(player.getUniqueId())) {
                promise.reject("You are not the party leader");
                return;
            }

            playerManager.getPlayer(invited.getUniqueId()).ifPresentOrElse(invitedArenaPlayer -> {
                if (!invitedArenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                    promise.reject(invited.getName() + " is not in the lobby");
                    return;
                }

                if (team.isInvited(invited.getUniqueId())) {
                    promise.reject(invited.getName() + " already has a pending invite");
                    return;
                }

                team.getInvitedMembers().add(invited.getUniqueId());

                team.sendMessage(Component.text(player.getName(), NamedTextColor.AQUA)
                        .appendSpace().append(Component.text("has invited", NamedTextColor.GRAY))
                        .appendSpace().append(Component.text(invited.getName(), NamedTextColor.AQUA))
                        .appendSpace().append(Component.text("to the team", NamedTextColor.GRAY)));

                invited.sendMessage(Component.text("You have been invited to join", NamedTextColor.GRAY).appendSpace().append(Component.text(player.getName(), NamedTextColor.AQUA)).append(Component.text("'s team.", NamedTextColor.GRAY))
                        .appendSpace().append(Component.text("[Click to Accept]", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/team join " + player.getName()))));

                promise.resolve();
            }, () -> promise.reject("Player not found"));
        }, () -> promise.reject("Failed to obtain your Arena profile")), () -> promise.reject("You are not on a team"));
    }

    public void revokeInvite(Player player, String username, Promise promise) {

    }

    public void kickMember(Player player, String username, Promise promise) {

    }
}
