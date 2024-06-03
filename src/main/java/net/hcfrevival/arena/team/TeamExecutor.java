package net.hcfrevival.arena.team;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
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

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class TeamExecutor {
    @Getter public final TeamManager manager;

    public void createTeam(Player player, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                promise.reject("You are not in the lobby");
                return;
            }

            if (manager.getTeam(player).isPresent()) {
                promise.reject("You are already on a team");
                return;
            }

            arenaPlayer.setCurrentState(EPlayerState.LOBBY_IN_PARTY);

            Team team = new Team(arenaPlayer);
            manager.getTeamRepository().add(team);

            LobbyUtil.giveLobbyItems(manager.getPlugin(), player);

            promise.resolve();
        }, () -> promise.reject("Failed to load your profile"));
    }

    public void disbandTeam(Player player, Promise promise) {
        manager.getTeam(player).ifPresentOrElse(team -> {
            manager.getTeamRepository().remove(team);

            team.getFullMembers().forEach(teamMember -> {
                if (teamMember.getCurrentState().equals(EPlayerState.LOBBY_IN_PARTY) || teamMember.getCurrentState().equals(EPlayerState.LOBBY_IN_QUEUE)) {
                    teamMember.setCurrentState(EPlayerState.LOBBY);
                }

                teamMember.clearFriendlies();
                teamMember.getPlayer().ifPresent(teamPlayer -> LobbyUtil.giveLobbyItems(manager.getPlugin(), teamPlayer));
            });

            team.sendMessage(Component.text("Team Disbanded", NamedTextColor.YELLOW));
            promise.resolve();
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
            if (!team.isInvited(player.getUniqueId()) && !bypass && !team.isOpen()) {
                promise.reject("You have not been invited to join this team");
                return;
            }

            playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
                boolean giveLobbyItems = true;

                if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                    promise.reject("You are not in the lobby");
                    return;
                }

                if (manager.getTeam(arenaPlayer.getUniqueId()).isPresent()) {
                    promise.reject("You are already on a team");
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
                    LobbyUtil.giveLobbyItems(manager.getPlugin(), player);
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
                new Scheduler(manager.getPlugin()).sync(() -> team.getInvitedMembers().remove(invited.getUniqueId())).delay(30 * 20L).run();

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
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        boolean bypass = (player.hasPermission(APermissions.A_ADMIN) || player.hasPermission(APermissions.A_MOD));

        Player kickedPlayer = Bukkit.getPlayer(username);
        if (kickedPlayer == null || !kickedPlayer.isOnline()) {
            promise.reject("Player not found");
            return;
        }

        Optional<ArenaPlayer> kickedArenaPlayerQuery = playerManager.getPlayer(kickedPlayer.getUniqueId());
        if (kickedArenaPlayerQuery.isEmpty()) {
            promise.reject("Player not found");
            return;
        }

        Optional<Team> teamQuery = manager.getTeam(player);
        if (teamQuery.isEmpty()) {
            promise.reject("You are not on a team");
            return;
        }

        ArenaPlayer kickedArenaPlayer = kickedArenaPlayerQuery.get();
        Team team = teamQuery.get();
        if (!team.getLeader().getUniqueId().equals(player.getUniqueId()) && !bypass) {
            promise.reject("You do not have permission to perform this action");
            return;
        }

        if (!kickedArenaPlayer.isInLobby()) {
            promise.reject(kickedArenaPlayer.getUsername() + " is not in the lobby");
            return;
        }

        team.removeMember(kickedArenaPlayer);
        team.sendMessage(Component.text(player.getName(), NamedTextColor.AQUA)
                .appendSpace().append(Component.text("has kicked", NamedTextColor.GRAY))
                .appendSpace().append(Component.text(kickedPlayer.getName(), NamedTextColor.AQUA))
                .appendSpace().append(Component.text("from the team")));

        kickedPlayer.sendMessage(Component.text("You have been kicked from your team", NamedTextColor.YELLOW));

        promise.resolve();
    }

    public void toggleTeamOpen(Player player, Promise promise) {
        final boolean bypass = player.hasPermission(APermissions.A_MOD);

        manager.getTeam(player).ifPresentOrElse(team -> {
            if (!team.getLeader().getUniqueId().equals(player.getUniqueId()) && !bypass) {
                promise.reject("You are not the leader of the team");
                return;
            }

            team.setOpen(!team.isOpen());
            team.sendMessage(Component.text(player.getName(), NamedTextColor.AQUA)
                    .appendSpace().append(Component.text("has" + " " + (team.isOpen() ? "opened" : "closed") + " the team", NamedTextColor.GRAY))
            );

            promise.resolve();
        }, () -> promise.reject("You are not on a team"));
    }

    public void printTeamInfo(Player player, String username, Promise promise) {
        Player toQuery = Bukkit.getPlayer(username);

        if (toQuery == null || !toQuery.isOnline()) {
            promise.reject("Player not found");
            return;
        }

        manager.getTeam(toQuery).ifPresentOrElse(team -> {
            SessionManager sessionManager = (SessionManager) manager.getPlugin().getManagers().get(SessionManager.class);
            List<String> memberNames = Lists.newArrayList();
            team.getFullMembers().forEach(member -> memberNames.add(member.getUsername()));
            boolean ingame = sessionManager.getSession(team).isPresent();

            player.sendMessage(Component.text(team.getDisplayName(), NamedTextColor.AQUA));

            player.sendMessage(Component.text("Status", NamedTextColor.GRAY).append(Component.text(":", NamedTextColor.WHITE))
                    .appendSpace().append(Component.text((ingame) ? "In Match" : "Lobby", NamedTextColor.WHITE)));

            player.sendMessage(Component.text("Joinable", NamedTextColor.GRAY).append(Component.text(":"))
                    .appendSpace().append(Component.text((team.isOpen() ? "Yes" : "No"), (team.isOpen() ? NamedTextColor.GREEN : NamedTextColor.RED))));

            player.sendMessage(Component.text("Members", NamedTextColor.GRAY)
                    .append(Component.text(": " + Joiner.on(", ").join(memberNames), NamedTextColor.WHITE)));

            promise.resolve();
        }, () -> {
            promise.reject("Team not found");
        });
    }
}
