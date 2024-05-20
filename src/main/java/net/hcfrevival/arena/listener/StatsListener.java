package net.hcfrevival.arena.listener;

import gg.hcfactions.cx.event.PlayerSprintResetEvent;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.MatchFinishEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.session.ISession;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public record StatsListener(@Getter ArenaPlugin plugin) implements Listener {
    @EventHandler
    public void onMatchFinish(MatchFinishEvent event) {
        final ISession session = event.getSession();

        session.getPlayers().stream().filter(ap -> ap.getCurrentState().equals(EPlayerState.INGAME)).forEach(arenaPlayer -> {
            arenaPlayer.getPlayer().ifPresent(bukkitPlayer -> arenaPlayer.getStatHolder().storeFinalAttributes(bukkitPlayer));
        });
    }

    @EventHandler
    public void onSprintReset(PlayerSprintResetEvent event) {
        final Player player = event.getPlayer();
        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> {
            arenaPlayer.getStatHolder().addSprintResetHit();
            arenaPlayer.getStatHolder().addTotalHits();
        });
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final Player damaged)) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof final Projectile projectile) {
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager == null) {
            return;
        }

        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);

        playerManager.getPlayer(damager.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getStatHolder() == null) {
                return;
            }

            arenaPlayer.getStatHolder().addTotalDamage(Math.round(event.getFinalDamage()));
            arenaPlayer.getStatHolder().addTotalHits();
            arenaPlayer.getStatHolder().addCurrentCombo();
        });

        playerManager.getPlayer(damaged.getUniqueId()).ifPresent(arenaPlayer -> {
            if (arenaPlayer.getStatHolder() == null) {
                return;
            }

            arenaPlayer.getStatHolder().resetCurrentCombo();
        });
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof final Player player)) {
            return;
        }

        final PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
        final double intensity = event.getIntensity(player);

        playerManager.getPlayer(player.getUniqueId()).ifPresent(arenaPlayer -> arenaPlayer.getStatHolder().addPotionAccuracy(intensity));
    }
}
