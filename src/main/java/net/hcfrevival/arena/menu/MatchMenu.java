package net.hcfrevival.arena.menu;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.IMenuUpdater;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.menu.impl.PaginatedMenu;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.event.PlayerLeaveQueueEvent;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.player.impl.EPlayerState;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.session.ISession;
import net.hcfrevival.arena.session.impl.DuelSession;
import net.hcfrevival.arena.session.impl.RankedDuelSession;
import net.hcfrevival.arena.session.impl.TeamSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Getter
public final class MatchMenu extends PaginatedMenu<ISession> {
    public ArenaPlugin plugin;

    public MatchMenu(ArenaPlugin plugin, Player player, Collection<ISession> entries) {
        super(plugin, player, "Active Matches", 6, entries);
        this.plugin = plugin;
    }

    @Override
    public List<ISession> sort() {
        List<ISession> sorted = Lists.newArrayList(getEntries());
        sorted.sort(Comparator.comparingLong(ISession::getStartTimestamp));
        return sorted;
    }

    @Override
    public Clickable getItem(ISession session, int i) {
        ItemBuilder builder = new ItemBuilder()
                .setMaterial(Material.BOOK);

        String name = null;
        List<Component> lore = Lists.newArrayList();

        if (session instanceof TeamSession teamSession) {
            if (teamSession.getTeams().size() <= 2) {
                List<String> teamNames = Lists.newArrayList();
                teamSession.getTeams().forEach(team -> teamNames.add(team.getDisplayName()));
                name = Joiner.on(" vs. ").join(teamNames);
            } else {
                name = teamSession.getTeams().size() + " Team Duel";
            }
        } else if (session instanceof DuelSession duelSession) {
            name = duelSession.getPlayerA().getUsername() + " vs. " + duelSession.getPlayerB().getUsername();
        } else {
            name = "Unknown Duel";
        }

        lore.add(Component.text("Match Duration", NamedTextColor.GOLD).append(Component.text(":", NamedTextColor.YELLOW))
                .appendSpace().append(Component.text(Time.convertToHHMMSS(session.getDuration()), NamedTextColor.YELLOW)));

        lore.add(Component.text("Map", NamedTextColor.GOLD).append(Component.text(":", NamedTextColor.YELLOW))
                .appendSpace().append(Component.text(session.getArena().getOwner().getName(), NamedTextColor.YELLOW)));

        if (session instanceof RankedDuelSession) {
            lore.add(Component.text("Ranked", NamedTextColor.RED));
        } else if (session instanceof TeamSession) {
            lore.add(Component.text("Teamfight", NamedTextColor.AQUA));
        } else {
            lore.add(Component.text("Unranked", NamedTextColor.GREEN));
        }

        builder.setName(Component.text(name, NamedTextColor.GOLD));
        builder.addLoreComponents(lore);

        return new Clickable(builder.build(), i, click -> {
            PlayerManager playerManager = (PlayerManager) plugin.getManagers().get(PlayerManager.class);
            QueueManager queueManager = (QueueManager) plugin.getManagers().get(QueueManager.class);

            playerManager.getPlayer(player.getUniqueId()).ifPresentOrElse(arenaPlayer -> {
                if (!arenaPlayer.getCurrentState().equals(EPlayerState.LOBBY)) {
                    player.closeInventory();
                    player.sendMessage(Component.text("You are not in the lobby", NamedTextColor.RED));
                    return;
                }

                queueManager.getQueue(player).ifPresent(queue -> {
                    PlayerLeaveQueueEvent leaveEvent = new PlayerLeaveQueueEvent(player, queue);
                    Bukkit.getPluginManager().callEvent(leaveEvent);

                    if (!leaveEvent.isCancelled()) {
                        queueManager.getQueueRepository().remove(queue);
                    }
                });

                session.startSpectating(arenaPlayer);
                player.closeInventory();
            }, () -> {
                player.closeInventory();
                player.sendMessage(Component.text("Failed to load your Arena data", NamedTextColor.RED));
            });
        });
    }

    @Override
    public void open() {
        super.open();
        addUpdater(this::update, 20L);
    }
}
