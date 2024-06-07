package net.hcfrevival.arena.listener;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.MatchFinishEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.consumables.EConsumableApplicationType;
import net.hcfrevival.classes.events.ClassConsumeItemEvent;
import net.hcfrevival.classes.events.ClassHoldableUpdateEvent;
import net.hcfrevival.classes.events.ClassReadyEvent;
import net.hcfrevival.classes.events.RogueInvisibilityQueryEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public final class ClassListener implements Listener {
    @Getter public final ArenaPlugin plugin;

    @EventHandler
    public void onClassReady(ClassReadyEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)) {
                event.getPlayerClass().activate(player);
            }
        }, () -> player.sendMessage(Component.text("Failed to load your Arena data", NamedTextColor.RED)));
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onClassHoldable(ClassHoldableUpdateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        Player player = event.getPlayer();
        Set<UUID> toRemove = Sets.newHashSet();

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.INGAME)) {
                event.setCancelled(true);
            }
        }, () -> event.setCancelled(true));

        teamManager.getTeam(player).ifPresent(team -> event.getAffectedPlayers().forEach(affectedPlayerId -> {
            if (!team.isMember(affectedPlayerId)) {
                toRemove.add(affectedPlayerId);
            }
        }));

        toRemove.forEach(removedUUID -> event.getAffectedPlayers().remove(removedUUID));
    }

    @EventHandler
    public void onClassConsumeItem(ClassConsumeItemEvent event) {
        Player player = event.getPlayer();
        Set<UUID> affectedPlayers = event.getAffectedPlayers();
        Set<UUID> toRemove = Sets.newHashSet();
        EConsumableApplicationType applicationType = event.getConsumable().getApplicationType();

        if (applicationType.equals(EConsumableApplicationType.SELF)) {
            return;
        }

        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(player).ifPresentOrElse(team -> {
            if (applicationType.equals(EConsumableApplicationType.FRIENDLY)) {
                affectedPlayers.forEach(affectedPlayer -> {
                    Player affectedBukkitPlayer = Bukkit.getPlayer(affectedPlayer);

                    if (affectedBukkitPlayer == null
                            || !team.isMember(affectedBukkitPlayer.getUniqueId())
                            || !affectedBukkitPlayer.getGameMode().equals(GameMode.SURVIVAL)
                    ) {
                        toRemove.add(affectedPlayer);
                    }
                });
            }

            if (applicationType.equals(EConsumableApplicationType.ENEMY)) {
                affectedPlayers.forEach(affectedPlayer -> {
                    Player affectedBukkitPlayer = Bukkit.getPlayer(affectedPlayer);

                    if (affectedBukkitPlayer == null
                            || team.isMember(affectedBukkitPlayer.getUniqueId())
                            || !affectedBukkitPlayer.getGameMode().equals(GameMode.SURVIVAL)
                    ) {
                        toRemove.add(affectedPlayer);
                    }
                });
            }

            toRemove.forEach(affectedPlayers::remove);
        }, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onRogueInvisQuery(RogueInvisibilityQueryEvent event) {
        Player player = event.getPlayer();
        TeamManager teamManager = (TeamManager) plugin.getManagers().get(TeamManager.class);

        teamManager.getTeam(player).ifPresentOrElse(team -> {
            event.getWithinFullRadiusPlayers().removeIf(fullRadiusPlayer -> team.isMember(fullRadiusPlayer.getUniqueId()));
            event.getWithinPartialRadiusPlayers().removeIf(partialRadiusPlayer -> team.isMember(partialRadiusPlayer.getUniqueId()));
        }, () -> {
            event.getWithinPartialRadiusPlayers().clear();
            event.getWithinFullRadiusPlayers().clear();
        });
    }

    @EventHandler
    public void onMatchFinish(MatchFinishEvent event) {
        ClassService cs = (ClassService) plugin.getService(ClassService.class);

        if (cs == null) {
            return;
        }

        if (event.getSession() instanceof TeamSession teamSession) {
            teamSession.getTeams().forEach(team ->
                    team.getFullMembers().forEach(member ->
                            cs.getClassRepository().forEach(playerClass ->
                                    playerClass.getConfig().getConsumables().forEach(consumable ->
                                            consumable.getCooldowns().remove(member.getUniqueId())))));

            return;
        }

        if (event.getSession() instanceof DuelSession duelSession) {
            duelSession.getPlayers().forEach(player ->
                    cs.getClassRepository().forEach(playerClass ->
                            playerClass.getConfig().getConsumables().forEach(consumable ->
                                    consumable.getCooldowns().remove(player.getUniqueId()))));
        }
    }
}
