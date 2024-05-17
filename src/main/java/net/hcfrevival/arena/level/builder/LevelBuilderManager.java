package net.hcfrevival.arena.level.builder;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.LevelManager;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

public final class LevelBuilderManager extends ArenaManager {
    @Getter public final LevelManager levelManager;
    @Getter public final LevelBuilderExecutor executor;
    @Getter public final Set<ILevelBuilder<?>> builderRepository;

    public LevelBuilderManager(ArenaPlugin plugin, LevelManager levelManager) {
        super(plugin);
        this.levelManager = levelManager;
        this.executor = new LevelBuilderExecutor(this);
        this.builderRepository = Sets.newConcurrentHashSet();
    }

    public Optional<ILevelBuilder<?>> getBuilder(Player player) {
        return builderRepository.stream().filter(b -> b.getBuilderId().equals(player.getUniqueId())).findFirst();
    }
}
