package net.hcfrevival.arena.level.builder;

import gg.hcfactions.libs.base.consumer.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.level.IArena;
import net.hcfrevival.arena.level.builder.impl.GenericLevelBuilder;
import net.hcfrevival.arena.level.impl.DuelArena;
import net.hcfrevival.arena.level.impl.TeamArena;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class LevelBuilderExecutor {
    @Getter public LevelBuilderManager manager;

    public void createNewLevelContainer(Player player, String arenaName, boolean isTeamArena, Promise promise) {
        if (manager.getLevelManager().getArenaByName(arenaName).isPresent()) {
            promise.reject("Arena name is already in use");
            return;
        }

        if (isTeamArena) {
            TeamArena arena = new TeamArena(arenaName, Component.text(arenaName), player.getName());
            manager.getLevelManager().getArenaRepository().add(arena);
            promise.resolve();
            return;
        }

        DuelArena arena = new DuelArena(arenaName, Component.text(arenaName), player.getName());
        manager.getLevelManager().getArenaRepository().add(arena);
        promise.resolve();
    }

    public void startBuildingDuelLevel(Player player, String arenaName, Promise promise) {
        if (!player.hasPermission(APermissions.A_MOD)) {
            promise.reject("You do not have permission to perform this action");
            return;
        }

        Optional<IArena> arenaQuery = manager.getLevelManager().getArenaByName(arenaName);
        if (arenaQuery.isEmpty()) {
            promise.reject("Arena not found");
            return;
        }

        Optional<ILevelBuilder<?>> builderQuery = manager.getBuilder(player);
        if (builderQuery.isPresent()) {
            promise.reject("You are already building an arena. Type /arena build cancel to stop building your current arena");
            return;
        }

        GenericLevelBuilder builder = new GenericLevelBuilder(manager.getPlugin(), arenaName, player);
        manager.getBuilderRepository().add(builder);
        promise.resolve();
    }
}
