package net.hcfrevival.arena.level;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.builder.LevelBuilderManager;
import net.hcfrevival.arena.level.impl.*;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        // TODO: Testing, remove soon
        final PLocatable spawn1 = new PLocatable("world", 10.0, 64.0, 10.0, 0.0f, 0.0f);
        final PLocatable spawn2 = new PLocatable("world", -10.0, 64.0, -10.0, 0.0f, 0.0f);
        final PLocatable spec = new PLocatable("world", 25.0, 64.0, 25.0, 0.0f, 0.0f);
        final BLocatable regionA = new BLocatable("world", 50.0, 0.0, 50.0);
        final BLocatable regionB = new BLocatable("world", -50.0, 256.0, -50.0);

        final DuelArenaInstance inst = new DuelArenaInstance(UUID.randomUUID(), null,true, List.of(spawn1, spawn2), spec, new ArenaRegion(regionA, regionB));
        final DuelArena testArena = new DuelArena("TestArena", ChatColor.YELLOW + "Test Arena", "johnsama");
        testArena.registerInstance(inst);

        arenaRepository.add(testArena);
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
