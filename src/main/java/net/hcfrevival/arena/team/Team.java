package net.hcfrevival.arena.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Team {
    @Getter public final UUID uniqueId;
    @Getter public final ArenaPlayer leader;
    @Getter public final Set<ArenaPlayer> members;

    public Team(ArenaPlayer leader) {
        this.uniqueId = UUID.randomUUID();
        this.leader = leader;
        this.members = Sets.newConcurrentHashSet();
    }

    public boolean isMember(UUID uniqueId) {
        return members.stream().anyMatch(member -> member.getUniqueId().equals(uniqueId));
    }

    public boolean isMember(String username) {
        return members.stream().anyMatch(member -> member.getUsername().equalsIgnoreCase(username));
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

    public List<ArenaPlayer> getMembersByState(EPlayerState state) {
        return getFullMembers().stream().filter(m -> m.getCurrentState().equals(state)).collect(Collectors.toList());
    }

    public void sendMessage(String message) {
        getFullMembers().forEach(member -> {
            final Player player = Bukkit.getPlayer(member.getUniqueId());

            if (player != null) {
                player.sendMessage(message);
            }
        });
    }
}
