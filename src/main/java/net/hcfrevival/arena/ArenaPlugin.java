package net.hcfrevival.arena;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Maps;
import gg.hcfactions.cx.CXService;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import lombok.Getter;
import net.hcfrevival.arena.command.ArenaCommand;
import net.hcfrevival.arena.command.KitCommand;
import net.hcfrevival.arena.command.MatchCommand;
import net.hcfrevival.arena.command.TeamCommand;
import net.hcfrevival.arena.items.*;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.level.LevelManager;
import net.hcfrevival.arena.listener.*;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.queue.QueueManager;
import net.hcfrevival.arena.session.SessionManager;
import net.hcfrevival.arena.team.TeamManager;
import net.hcfrevival.arena.timer.TimerManager;

import java.util.Map;

public final class ArenaPlugin extends AresPlugin {
    @Getter public Map<Class<? extends ArenaManager>, ArenaManager> managers;
    @Getter public ArenaConfig configuration;

    @Override
    public void onEnable() {
        super.onEnable();

        configuration = new ArenaConfig(this);
        configuration.load();

        // protocollib
        registerProtocolLibrary(ProtocolLibrary.getProtocolManager());

        // services
        // custom item service
        final CustomItemService cis = new CustomItemService(this);
        cis.registerNewItem(new UnrankedQueueItem(this));
        cis.registerNewItem(new RankedQueueItem(this));
        cis.registerNewItem(new LeaveQueueItem(this));
        cis.registerNewItem(new EditKitItem());
        cis.registerNewItem(new CreatePartyItem(this));
        cis.registerNewItem(new CustomKitBook(this));
        cis.registerNewItem(new DefaultKitBook(this));
        cis.registerNewItem(new DisbandTeamItem(this));
        cis.registerNewItem(new TeamListItem(this));
        registerService(cis);
        registerService(new CXService(this));

        startServices();

        // commands
        registerCommand(new ArenaCommand(this));
        registerCommand(new MatchCommand(this));
        registerCommand(new KitCommand(this));
        registerCommand(new TeamCommand(this));

        // managers
        registerManager(new PlayerManager(this));
        registerManager(new QueueManager(this));
        registerManager(new LevelManager(this));
        registerManager(new SessionManager(this));
        registerManager(new TimerManager(this));
        registerManager(new KitManager(this));
        registerManager(new TeamManager(this));
        managers.values().forEach(ArenaManager::onEnable);

        // listeners
        registerListener(new LobbyListener(this));
        registerListener(new PlayerDataListener(this));
        registerListener(new QueueListener(this));
        registerListener(new SpectatorListener(this));
        registerListener(new MatchListener(this));
        registerListener(new StatsListener(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();

        managers.values().forEach(ArenaManager::onDisable);
        managers.clear();
    }

    public void registerManager(ArenaManager manager) {
        if (managers == null) {
            managers = Maps.newHashMap();
        }

        managers.put(manager.getClass(), manager);
    }
}
