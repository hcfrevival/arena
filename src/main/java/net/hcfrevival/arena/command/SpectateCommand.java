package net.hcfrevival.arena.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@Getter
@CommandAlias("spectate|spec")
@AllArgsConstructor
public final class SpectateCommand extends BaseCommand {
    public final ArenaPlugin plugin;

    @Default
    @Description("Spectate a player")
    @Syntax("<player>")
    public void onSpectate(Player player, String username) {
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(SessionManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                player.sendMessage(Component.text("You are not in the lobby", NamedTextColor.RED));
                return;
            }

            playerManager.getPlayer(username).ifPresentOrElse(targetArenaPlayer ->
                    targetArenaPlayer.getPlayer().ifPresentOrElse(targetPlayer ->
                            sessionManager.getSession(targetPlayer).ifPresentOrElse(session -> {

                                session.startSpectating(arenaPlayer);
                                session.sendMessage(Component.text(player.getName() + " is now spectating this match", NamedTextColor.GRAY));

            }, () -> player.sendMessage(Component.text(targetPlayer.getName() + " is not in a match", NamedTextColor.RED))), () -> player.sendMessage(Component.text("Player not found", NamedTextColor.RED))), () -> player.sendMessage(Component.text("Player not found", NamedTextColor.RED)));
        }, () -> player.sendMessage(Component.text("Failed to load your Arena data", NamedTextColor.RED)));
    }

    @Subcommand("leave|stop")
    @Description("Stop spectating a match")
    public void onStopSpectating(Player player) {
        PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        SessionManager sessionManager = (SessionManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
            if (!arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE) && !arenaPlayer.getCurrentState().equals(EPlayerState.SPECTATE_DEAD)) {
                player.sendMessage(Component.text("You are not spectating a match", NamedTextColor.RED));
                return;
            }

            sessionManager.getSession(player).ifPresentOrElse(session -> {
                session.stopSpectating(arenaPlayer);
                session.sendMessage(Component.text(player.getName() + " is no longer spectating", NamedTextColor.GRAY));
                player.sendMessage(Component.text("You have exited spectator mode", NamedTextColor.AQUA));
            }, () -> player.sendMessage(Component.text("Failed to find your session", NamedTextColor.RED)));
        }, () -> player.sendMessage(Component.text("Failed to load your Arena data", NamedTextColor.RED)));
    }
}
