package net.hcfrevival.arena.level;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.builder.LevelBuilderManager;
import net.hcfrevival.arena.level.impl.DuelArena;
import net.hcfrevival.arena.level.impl.DuelArenaInstance;
import net.hcfrevival.arena.level.impl.TeamArena;
import net.hcfrevival.arena.level.impl.TeamArenaInstance;

import java.util.List;
import java.util.Optional;

public final class LevelManager extends ArenaManager {
    @Getter public final LevelBuilderManager levelBuilderManager;
    @Getter public final List<IArena> arenaRepository;

    public LevelManager(ArenaPlugin plugin) {
        super(plugin);
        this.levelBuilderManager = new LevelBuilderManager(plugin);
        this.arenaRepository = Lists.newArrayList();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        levelBuilderManager.onEnable();

        loadArenas();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        levelBuilderManager.onDisable();

        arenaRepository.clear();
    }

    private void loadArenas() {

    }

    private void saveArena(IArena arena) {

    }

    private void deleteArena(IArena arena) {

    }

    /**
     * Queries all DuelArenaInstances to find an available one
     * @return Optional of DuelArenaInstance
     */
    public Optional<DuelArenaInstance> getAvailableDuelInstance() {
        final DuelArena duelArena = (DuelArena) arenaRepository.stream().filter(a -> a instanceof DuelArena && a.hasAvailableInstances()).findFirst().orElse(null);

        if (duelArena == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((DuelArenaInstance) duelArena.getAvailableInstance().orElse(null));
    }

    /**
     * Queries all TeamArenaInstances to find an available one
     * @return Optional of TeamArenaInstance
     */
    public Optional<TeamArenaInstance> getAvailableTeamInstance() {
        final TeamArena teamArena = (TeamArena) arenaRepository.stream().filter(a -> a instanceof TeamArena && a.hasAvailableInstances()).findFirst().orElse(null);

        if (teamArena == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((TeamArenaInstance) teamArena.getAvailableInstance().orElse(null));
    }
}
