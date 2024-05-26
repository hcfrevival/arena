package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.APermissions;
import net.hcfrevival.arena.ArenaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("spawn")
public final class SpawnCommand extends BaseCommand {
    @Getter public final ArenaPlugin plugin;

    @Default
    @Description("Return to spawn")
    @CommandPermission(APermissions.A_MOD)
    public void onSpawn(Player player) {
        player.teleport(plugin.getConfiguration().getSpawnLocation().getBukkitLocation());
        player.sendMessage(Component.text("Returned to spawn", NamedTextColor.AQUA));
    }

    @Subcommand("set")
    @Description("Update the spawn location")
    @CommandPermission(APermissions.A_ADMIN)
    public void onSpawnSet(Player player) {
        plugin.getConfiguration().setSpawnLocation(new PLocatable(player));
        player.sendMessage(Component.text("Spawn has been updated", NamedTextColor.GREEN));
    }
}
