package net.hcfrevival.arena.session;

import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.base.util.Time;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.player.impl.ArenaPlayer;
import net.hcfrevival.arena.session.request.DuelRequestManager;
import net.hcfrevival.arena.team.impl.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public interface IDuelRequest<T> {
    DuelRequestManager getManager();
    UUID getId();
    T getSender();
    T getReceiver();
    EGamerule getGamerule();
    long getExpire();

    default void accept() {
        getManager().getExecutor().accept(this, new Promise() {
            @Override
            public void resolve() {}

            @Override
            public void reject(String s) {
                if (getReceiver() instanceof ArenaPlayer arenaPlayer) {
                    arenaPlayer.getPlayer().ifPresent(player -> player.sendMessage(Component.text("Failed to accept duel: " + s, NamedTextColor.RED)));
                } else if (getReceiver() instanceof Team team) {
                    team.sendMessage(Component.text("Failed to accept duel: " + s, NamedTextColor.RED));
                }
            }
        });
    }

    default void decline(Promise promise) {
        getManager().getExecutor().decline(this, new Promise() {
            @Override
            public void resolve() {
                if (getReceiver() instanceof ArenaPlayer arenaPlayer) {
                    arenaPlayer.getPlayer().ifPresent(player -> player.sendMessage(Component.text("Duel request has been declined", NamedTextColor.YELLOW)));
                } else if (getReceiver() instanceof Team team) {
                    team.sendMessage(Component.text("Duel request has been declined", NamedTextColor.YELLOW));
                }

                promise.resolve();
            }

            @Override
            public void reject(String s) {
                if (getReceiver() instanceof ArenaPlayer arenaPlayer) {
                    arenaPlayer.getPlayer().ifPresent(player -> player.sendMessage(Component.text("Failed to decline duel: " + s, NamedTextColor.RED)));
                } else if (getReceiver() instanceof Team team) {
                    team.sendMessage(Component.text("Failed to decline duel: " + s, NamedTextColor.RED));
                }

                promise.reject(s);
            }
        });
    }

    default boolean isExpired() {
        return getExpire() <= Time.now();
    }
}
