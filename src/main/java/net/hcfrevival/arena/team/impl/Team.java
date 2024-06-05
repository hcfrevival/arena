package net.hcfrevival.arena.team.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.team.loadout.TeamLoadoutConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Team {
    @Getter public final UUID uniqueId;
    @Getter @Setter public ArenaPlayer leader;
    @Getter public final Set<ArenaPlayer> members;
    @Getter public final Set<UUID> invitedMembers;
    @Getter @Setter public boolean open;
    @Getter public TeamLoadoutConfig loadoutConfig;

    public Team(ArenaPlayer leader) {
        this.uniqueId = UUID.randomUUID();
        this.leader = leader;
        this.members = Sets.newConcurrentHashSet();
        this.invitedMembers = Sets.newConcurrentHashSet();
        this.open = false;
        this.loadoutConfig = new TeamLoadoutConfig(this);

        leader.getPlayer().ifPresent(leader::addFriendly);
    }

    public String getDisplayName() {
        return leader.getUsername() + "'s Team";
    }

    public List<ArenaPlayer> getFullMembers() {
        final List<ArenaPlayer> res = Lists.newArrayList();
        res.add(leader);
        res.addAll(members);
        return res;
    }

    public Optional<ArenaPlayer> getNewLeader() {
        List<ArenaPlayer> members = getFullMembers();

        if (members.size() <= 1) {
            return Optional.empty();
        }

        return members.stream().filter(member -> !member.equals(leader)).findFirst();
    }

    public List<ArenaPlayer> getMembersByState(EPlayerState state) {
        return getFullMembers().stream().filter(m -> m.getCurrentState().equals(state)).collect(Collectors.toList());
    }

    public boolean isMember(UUID uniqueId) {
        return getFullMembers().stream().anyMatch(member -> member.getUniqueId().equals(uniqueId));
    }

    public boolean isMember(String username) {
        return getFullMembers().stream().anyMatch(member -> member.getUsername().equalsIgnoreCase(username));
    }

    public boolean isInvited(UUID uniqueId) {
        return invitedMembers.contains(uniqueId);
    }

    /**
     * @deprecated Use sendMessage#Component
     * @param message
     */
    public void sendMessage(String message) {
        getFullMembers().forEach(member -> {
            final Player player = Bukkit.getPlayer(member.getUniqueId());

            if (player != null) {
                player.sendMessage(message);
            }
        });
    }

    public void sendMessage(Component component) {
        getFullMembers().forEach(member -> member.getPlayer().ifPresent(player -> player.sendMessage(component)));
    }

    public void teleport(Location loc) {
        getFullMembers().forEach(member -> {
            final Player player = Bukkit.getPlayer(member.getUniqueId());

            if (player != null) {
                player.teleport(loc);
            }
        });
    }

    public void addMember(ArenaPlayer arenaPlayer) {
        if (isMember(arenaPlayer.getUniqueId())) {
            return;
        }

        members.add(arenaPlayer);

        getFullMembers().forEach(member -> {
            member.getPlayer().ifPresent(arenaPlayer::addFriendly);
            arenaPlayer.getPlayer().ifPresent(member::addFriendly);
        });
    }

    public void removeMember(ArenaPlayer arenaPlayer) {
        if (!isMember(arenaPlayer.getUniqueId())) {
            return;
        }

        getFullMembers().forEach(member -> arenaPlayer.getPlayer().ifPresent(member::removeFriendly));
        members.remove(arenaPlayer);
        loadoutConfig.getLoadoutRepository().remove(arenaPlayer.getUniqueId());
    }

    public void disband() {
        getFullMembers().forEach(ArenaPlayer::clearFriendlies);
        setLeader(null);
        getMembers().clear();
    }
}
