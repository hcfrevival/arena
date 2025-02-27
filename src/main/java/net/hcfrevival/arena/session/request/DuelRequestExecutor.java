package net.hcfrevival.arena.session.request;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.consumer.FailablePromise;
import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.menu.KitSelectMenu;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.session.IDuelRequest;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.session.request.impl.PlayerDuelRequest;
import net.hcfrevival.arena.session.request.impl.TeamDuelRequest;
import net.hcfrevival.arena.team.impl.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DuelRequestExecutor {
    @Getter public final DuelRequestManager manager;

    public void promptDuelKitSelect(Player sender, String receiverName, Promise promise) {
        PlayerManager playerManager = (PlayerManager) manager.getPlugin().getManagers().get(PlayerManager.class);
        Optional<ArenaPlayer> senderQuery = playerManager.getPlayer(sender.getUniqueId());
        Optional<ArenaPlayer> receiverQuery = playerManager.getPlayer(receiverName);

        if (senderQuery.isEmpty()) {
            promise.reject("Failed to load your Arena profile");
            return;
        }

        if (receiverQuery.isEmpty()) {
            promise.reject("Player not found");
            return;
        }

        ArenaPlayer senderArenaPlayer = senderQuery.get();
        if (!senderArenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
            promise.reject(senderArenaPlayer.getUsername() + " is not in the lobby");
            return;
        }

        ArenaPlayer receiverArenaPlayer = receiverQuery.get();
        if (!receiverArenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
            promise.reject(receiverArenaPlayer.getUsername() + " is not in the lobby");
            return;
        }

        KitSelectMenu menu = new KitSelectMenu(manager.getPlugin(), sender, new FailablePromise<>() {
            @Override
            public void resolve(EGamerule gamerule) {
                send(senderQuery.get(), receiverQuery.get(), gamerule, promise);
            }

            @Override
            public void reject(String s) {
                promise.reject(s);
            }
        });

        menu.open();
    }

    public <T> void send(T sender, T receiver, EGamerule gamerule, Promise promise) {
        if (sender instanceof final ArenaPlayer playerSender) {
            ArenaPlayer playerReceiver = (ArenaPlayer) receiver;
            PlayerDuelRequest req = new PlayerDuelRequest(manager, playerSender, playerReceiver, gamerule);

            playerSender.getPlayer().ifPresent(p -> p.sendMessage(
                    Component.text("You have sent a", NamedTextColor.GRAY)
                            .appendSpace().append(gamerule.getDisplayNameComponent())
                            .appendSpace().append(Component.text("Duel Request to"))
                            .appendSpace().append(Component.text(playerReceiver.getUsername(), NamedTextColor.AQUA))));

            playerReceiver.getPlayer().ifPresent(p -> p.sendMessage(Component.text(playerSender.getUsername(), NamedTextColor.AQUA)
                    .appendSpace().append(Component.text("has sent you a", NamedTextColor.GRAY))
                    .appendSpace().append(gamerule.getDisplayNameComponent())
                    .appendSpace().append(Component.text("duel request", NamedTextColor.GRAY))
                    .appendSpace().append(Component.text("[Accept]", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/duel accept " + req.getId().toString())))));

            manager.getRequestRepository().add(req);
            promise.resolve();
        }

        else if (sender instanceof final Team teamSender) {
            Team teamReceiver = (Team) receiver;
            TeamDuelRequest req = new TeamDuelRequest(manager, teamSender, teamReceiver, gamerule);

            teamSender.sendMessage(Component.text("You have sent a", NamedTextColor.GRAY)
                    .appendSpace().append(gamerule.getDisplayNameComponent())
                    .appendSpace().append(Component.text("Duel Request to"))
                    .appendSpace().append(Component.text(teamReceiver.getDisplayName(), NamedTextColor.AQUA)));

            teamReceiver.sendMessage(Component.text(teamSender.getDisplayName(), NamedTextColor.AQUA)
                    .appendSpace().append(Component.text("has sent you a", NamedTextColor.GRAY))
                    .appendSpace().append(gamerule.getDisplayNameComponent())
                    .appendSpace().append(Component.text("duel request", NamedTextColor.GRAY))
                    .appendSpace().append(Component.text("[Accept]", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/duel accept " + req.getId().toString()))));

            manager.getRequestRepository().add(req);
            promise.resolve();
        }
    }

    public <T> void accept(IDuelRequest<T> request, Promise promise) {
        if (request instanceof final PlayerDuelRequest playerRequest) {
            QueueManager queueManager = (QueueManager) manager.getPlugin().getManagers().get(QueueManager.class);

            if (!playerRequest.getReceiver().isInLobby()) {
                promise.reject("Receiving player is not in the lobby");
                return;
            }

            if (!playerRequest.getSender().isInLobby()) {
                promise.reject("Sender player is not in the lobby");
                return;
            }

            Player senderPlayer = playerRequest.getSender().getPlayer().orElseThrow(NullPointerException::new);
            Player receiverPlayer = playerRequest.getReceiver().getPlayer().orElseThrow(NullPointerException::new);

            queueManager.getQueue(senderPlayer).ifPresent(senderQueue -> queueManager.getQueueRepository().remove(senderQueue));
            queueManager.getQueue(receiverPlayer).ifPresent(receiverQueue -> queueManager.getQueueRepository().remove(receiverQueue));

            List<IDuelRequest<?>> toRemove = Lists.newArrayList();
            for (IDuelRequest<?> req : manager.getSessionManager().getDuelRequestManager().getRequestRepository()) {
                if (req instanceof PlayerDuelRequest playerDuelRequest) {
                    if (
                            playerDuelRequest.getSender().getUniqueId().equals(senderPlayer.getUniqueId())
                                    || playerDuelRequest.getSender().getUniqueId().equals(receiverPlayer.getUniqueId())
                    ) {
                        toRemove.add(req);
                        continue;
                    }

                    if (
                            playerDuelRequest.getReceiver().getUniqueId().equals(senderPlayer.getUniqueId())
                            || playerDuelRequest.getReceiver().getUniqueId().equals(receiverPlayer.getUniqueId())
                    ) {
                        toRemove.add(req);
                    }
                }
            }

            // Remove all pending duel requests
            toRemove.forEach(manager.getSessionManager().getDuelRequestManager().getRequestRepository()::remove);

            Optional<DuelSession> sessionAttempt = manager.getSessionManager().createDuelSession(
                    request.getGamerule(),
                    playerRequest.getSender(),
                    playerRequest.getReceiver(),
                    false
            );

            if (sessionAttempt.isEmpty()) {
                manager.getPlugin().getAresLogger().error("Session Attempt is empty");
                promise.reject("Failed to create session");
                return;
            }

            manager.getSessionManager().startSession(sessionAttempt.get());
            promise.resolve();
        }

        else if (request instanceof final TeamDuelRequest teamRequest) {
            if (!teamRequest.getReceiver().getLeader().isInLobby()) {
                promise.reject("Receiving team is not in the lobby");
                return;
            }

            if (!teamRequest.getSender().getLeader().isInLobby()) {
                promise.reject("Sending team is not in the lobby");
                return;
            }

            Optional<TeamSession> sessionAttempt = manager.getSessionManager().createTeamSession(
                    request.getGamerule(),
                    List.of(teamRequest.getSender(), teamRequest.getReceiver())
            );

            if (sessionAttempt.isEmpty()) {
                manager.getPlugin().getAresLogger().error("Session Attempt (Team) is empty");
                promise.reject("Failed to create session");
                return;
            }

            manager.getSessionManager().startSession(sessionAttempt.get());
            promise.resolve();
        }
    }

    public void decline(IDuelRequest<?> request, Promise promise) {
        request.decline(promise);
    }
}
