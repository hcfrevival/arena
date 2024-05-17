package net.hcfrevival.arena.level.builder.impl;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.location.impl.BLocatable;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.level.builder.ERegionCorner;
import net.hcfrevival.arena.level.builder.ILevelBuilder;
import net.hcfrevival.arena.level.builder.LevelBuilderManager;
import net.hcfrevival.arena.level.impl.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class GenericLevelBuilder implements ILevelBuilder<DuelArenaInstance> {
    @Getter public final LevelBuilderManager builderManager;
    @Getter public final UUID builderId;
    @Setter public IArena owner;
    @Getter @Setter public EBuildStage buildStage;
    public PLocatable spectatorSpawnPosition;
    public final List<PLocatable> spawnPositions;
    public final ArenaRegion arenaRegion;

    public GenericLevelBuilder(ArenaPlugin plugin, String arenaName, Player builder) {
        LevelManager levelManager = (LevelManager) plugin.getManagers().get(LevelManager.class);
        Optional<IArena> arenaQuery = levelManager.getArenaRepository().stream().filter(a -> a.getName().equalsIgnoreCase(arenaName)).findFirst();

        if (arenaQuery.isEmpty()) {
            throw new NullPointerException("Attempted to initialize a level builder with an invalid arena name");
        }

        this.builderManager = levelManager.getLevelBuilderManager();
        this.builderId = builder.getUniqueId();
        this.owner = arenaQuery.get();
        this.buildStage = EBuildStage.SPECTATOR_SPAWN;
        this.spawnPositions = Lists.newArrayList();
        this.spectatorSpawnPosition = null;
        this.arenaRegion = new ArenaRegion(null, null);

        builder.sendMessage(Component.text("First, set the Spectator Spawn Position. To do this type /arena build set while standing where you want the position to be", NamedTextColor.YELLOW));
    }

    @Override
    public void setSpectatorSpawnPosition(PLocatable locatable) {
        this.spectatorSpawnPosition = locatable;
        this.buildStage = EBuildStage.PLAYER_SPAWN;

        getPlayer().ifPresent(player -> {
            player.sendMessage(Component.text("Spectator spawn position has been set", NamedTextColor.AQUA));
            player.sendMessage(Component.text("Next, set all spawn positions for individual players or entire teams", NamedTextColor.YELLOW));
        });
    }

    @Override
    public void addSpawnPosition(PLocatable location) {
        ILevelBuilder.super.addSpawnPosition(location);

        getPlayer().ifPresent(player -> {
            player.sendMessage(Component.text("Added spawn position", NamedTextColor.AQUA));
            player.sendMessage(Component.text("When you are done setting positions type /arena build setpos done", NamedTextColor.YELLOW));
        });
    }

    public void finalizePlayerSpawnPositions() {
        this.buildStage = EBuildStage.CORNER_A;

        getPlayer().ifPresent(player -> {
            player.sendMessage(Component.text("Player spawn positions have been finalized", NamedTextColor.AQUA));
            player.sendMessage(Component.text("Next, set the first position of this Arena Instance's playable region", NamedTextColor.YELLOW));
        });
    }

    @Override
    public void setArenaRegionCorner(BLocatable location, ERegionCorner corner) {
        ILevelBuilder.super.setArenaRegionCorner(location, corner);

        if (corner.equals(ERegionCorner.A)) {
            this.buildStage = EBuildStage.CORNER_B;

            getPlayer().ifPresent(player -> {
                player.sendMessage(Component.text("Arena region A has been set", NamedTextColor.AQUA));
                player.sendMessage(Component.text("Next, set the second position of this Arena Instance's playable region", NamedTextColor.YELLOW));
            });

            return;
        }

        DuelArenaInstance instance = build();
        if (instance == null) {
            getPlayer().ifPresent(player -> player.sendMessage(Component.text("Failed to build instance", NamedTextColor.RED)));
            return;
        }

        owner.registerInstance(instance);
        builderManager.getLevelManager().saveArena(owner);
        builderManager.getBuilderRepository().remove(this);
        getPlayer().ifPresent(player -> player.sendMessage(Component.text("Instance has been created", NamedTextColor.GREEN)));
    }

    @Override
    public DuelArenaInstance build() {
        if (getOwner() instanceof final TeamArena teamArena) {
            return new TeamArenaInstance(UUID.randomUUID(), teamArena, true, getSpawnPositions(), getSpectatorSpawnPosition(), getArenaRegion());
        }

        else if (getOwner() instanceof final DuelArena duelArena) {
            return new DuelArenaInstance(UUID.randomUUID(), duelArena, true, getSpawnPositions(), getSpectatorSpawnPosition(), getArenaRegion());
        }

        return null;
    }

    public enum EBuildStage {
        SPECTATOR_SPAWN,
        PLAYER_SPAWN,
        CORNER_A,
        CORNER_B
    }
}
