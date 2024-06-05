package net.hcfrevival.arena.team.loadout;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.team.impl.Team;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
public final class TeamLoadoutConfig {
    public final Team parent;
    public final Map<UUID, ELoadoutValue> loadoutRepository;

    public TeamLoadoutConfig(Team parent) {
        this.parent = parent;
        this.loadoutRepository = Maps.newConcurrentMap();
    }

    public ELoadoutValue getLoadoutValue(ArenaPlayer arenaPlayer) {
        return getLoadoutValue(arenaPlayer.getUniqueId());
    }

    public ELoadoutValue getLoadoutValue(Player player) {
        return getLoadoutValue(player.getUniqueId());
    }

    public ELoadoutValue getLoadoutValue(UUID uuid) {
        return loadoutRepository.getOrDefault(uuid, ELoadoutValue.NETHERITE);
    }

    public void setLoadoutValue(ArenaPlayer arenaPlayer, ELoadoutValue value) {
        setLoadoutValue(arenaPlayer.getUniqueId(), value);
    }

    public void setLoadoutValue(Player player, ELoadoutValue value) {
        setLoadoutValue(player.getUniqueId(), value);
    }

    public void setLoadoutValue(UUID uuid, ELoadoutValue value) {
        if (!parent.isMember(uuid)) {
            throw new IllegalArgumentException("Attempted to set a loadout value for a player that is not on the team");
        }

        loadoutRepository.put(uuid, value);
    }

    public void performCleanup() {
        Set<UUID> toRemove = Sets.newHashSet();

        loadoutRepository.keySet().forEach(uuid -> {
            if (!parent.isMember(uuid)) {
                toRemove.add(uuid);
            }
        });

        toRemove.forEach(loadoutRepository::remove);
    }

    @Getter
    @AllArgsConstructor
    public enum ELoadoutValue {
        NETHERITE(false),
        DIVER(false),
        BARD(false),
        ROGUE(false),
        ARCHER(false),
        GUARDIAN(true);

        public final boolean disabled;

        public static Optional<IClass> getAssociatedClass(ArenaPlugin plugin, ELoadoutValue value) {
            ClassService cs = (ClassService) plugin.getService(ClassService.class);

            if (cs == null) {
                return Optional.empty();
            }

            return cs.getClassByName(Strings.capitalize(value.name().toLowerCase()));
        }
    }
}
