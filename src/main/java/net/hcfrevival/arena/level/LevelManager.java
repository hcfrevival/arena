package net.hcfrevival.arena.level;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.location.ILocatable;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.utils.Configs;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.builder.LevelBuilderManager;
import net.hcfrevival.arena.level.impl.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class LevelManager extends ArenaManager {
    @Getter public final LevelBuilderManager levelBuilderManager;
    @Getter public final List<IArena> arenaRepository;

    public LevelManager(ArenaPlugin plugin) {
        super(plugin);
        this.levelBuilderManager = new LevelBuilderManager(plugin, this);
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

    public void loadArenas() {
        YamlConfiguration conf = plugin.loadConfiguration("arenas");

        if (!arenaRepository.isEmpty()) {
            arenaRepository.clear();
        }

        if (conf.get("data") == null) {
            plugin.getAresLogger().warn("Could not found any arenas in arenas.yml. Skipping...");
            return;
        }

        for (String arenaName : Objects.requireNonNull(conf.getConfigurationSection("data")).getKeys(false)) {
            String path = "data." + arenaName + ".";
            String displayNameUnformatted = conf.getString(path + "display_name");
            Component formattedDisplayName = LegacyComponentSerializer.legacySection().deserialize(Objects.requireNonNull(displayNameUnformatted));
            boolean isTeamArena = conf.getBoolean(path + "team");
            String authors = conf.getString(path + "authors");
            IArena arena = (isTeamArena)
                    ? new TeamArena(arenaName, formattedDisplayName, authors)
                    : new DuelArena(arenaName, formattedDisplayName, authors);

            if (conf.get(path + "instances") == null) {
                plugin.getAresLogger().warn("Could not find any instances for Arena {}", arenaName);
                continue;
            }

            for (String instanceId : Objects.requireNonNull(conf.getConfigurationSection(path + "instances")).getKeys(false)) {
                String instPath = path + "instances." + instanceId + ".";
                UUID uuid = UUID.fromString(instanceId);
                PLocatable spectatorSpawn = Configs.parsePlayerLocation(conf, instPath + "spectator_spawnpoint");
                List<PLocatable> playerSpawns = Lists.newArrayList();
                ArenaRegion region = new ArenaRegion(Configs.parseBlockLocation(conf, instPath + "region.a"), Configs.parseBlockLocation(conf, instPath + "region.b"));

                if (conf.getConfigurationSection(instPath + "spawnpoints") == null) {
                    plugin.getAresLogger().warn("Could not find any spawnpoints for Arena Instance {}", instanceId);
                    continue;
                }

                for (String spawnId : Objects.requireNonNull(conf.getConfigurationSection(instPath + "spawnpoints")).getKeys(false)) {
                    PLocatable spawnpoint = Configs.parsePlayerLocation(conf, instPath + "spawnpoints." + spawnId);
                    playerSpawns.add(spawnpoint);
                }

                if (isTeamArena) {
                    TeamArenaInstance inst = new TeamArenaInstance(uuid, (TeamArena) arena, true, playerSpawns, spectatorSpawn, region);
                    arena.registerInstance(inst);
                    continue;
                }

                DuelArenaInstance inst = new DuelArenaInstance(uuid, (DuelArena) arena, true, playerSpawns, spectatorSpawn, region);
                arena.registerInstance(inst);
            }

            arenaRepository.add(arena);
        }

        plugin.getAresLogger().info("Loaded {} Arenas", arenaRepository.size());
    }

    public void saveArenaInstance(IArenaInstance instance) {
        YamlConfiguration conf = plugin.loadConfiguration("arenas");
        String path = "data." + instance.getOwner().getName() + ".instances." + instance.getUniqueId().toString() + ".";

        Configs.writePlayerLocation(conf, path + "spectator_spawnpoint", instance.getSpectatorSpawnpoint());
        Configs.writeBlockLocation(conf, path + "region.a", instance.getRegion().getCornerA());
        Configs.writeBlockLocation(conf, path + "region.b", instance.getRegion().getCornerB());

        for (PLocatable spawnpoint : instance.getSpawnpoints()) {
            Configs.writePlayerLocation(conf, path + "spawnpoints." + UUID.randomUUID(), spawnpoint);
        }

        plugin.saveConfiguration("arenas", conf);
        plugin.getAresLogger().info("Saved {} Arena Instance", instance.getUniqueId().toString());
    }

    public void saveArena(IArena arena, boolean saveInstances) {
        YamlConfiguration conf = plugin.loadConfiguration("arenas");
        String path = "data." + arena.getName() + ".";

        conf.set(path + "display_name", LegacyComponentSerializer.legacySection().serialize(arena.getDisplayName()));
        conf.set(path + "team", (arena instanceof TeamArena));
        conf.set(path + "authors", arena.getAuthors());

        if (saveInstances) {
            arena.getInstances().forEach(inst -> {
                String instPath = path + "instances." + inst.getUniqueId().toString() + ".";

                Configs.writePlayerLocation(conf, instPath + "spectator_spawnpoint", inst.getSpectatorSpawnpoint());
                Configs.writeBlockLocation(conf, instPath + "region.a", inst.getRegion().getCornerA());
                Configs.writeBlockLocation(conf, instPath + "region.b", inst.getRegion().getCornerB());

                inst.getSpawnpoints().forEach(spawnpoint -> {
                    UUID id = UUID.randomUUID();
                    Configs.writePlayerLocation(conf, instPath + "spawnpoints." + id, spawnpoint);
                });
            });
        }

        plugin.saveConfiguration("arenas", conf);
        plugin.getAresLogger().info("Saved {} Arena", arena.getName());
    }

    public void deleteArenaInstance(IArenaInstance instance) {
        YamlConfiguration conf = plugin.loadConfiguration("arenas");
        conf.set("data." + instance.getOwner().getName() + "." + instance.getUniqueId().toString(), null);
        plugin.saveConfiguration("arenas", conf);
        plugin.getAresLogger().info("Deleted an instance from {}", instance.getOwner().getName());
    }

    public void deleteArena(IArena arena) {

    }

    /**
     * Queries all arenas to find one that matches the provided name
     * @param name Name to query
     * @return Optional of IArena
     */
    public Optional<IArena> getArenaByName(String name) {
        return arenaRepository.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Queries an Arena Instance by location
     * @param locatable Location
     * @return Optional of Arena Instance
     */
    public Optional<IArenaInstance> getInstanceByLocation(ILocatable locatable) {
        for (IArena arena : arenaRepository) {
            for (IArenaInstance instance : arena.getInstances()) {
                if (instance.getRegion().isInside(locatable, false)) {
                    return Optional.of(instance);
                }
            }
        }

        return Optional.empty();
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
