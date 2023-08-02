package net.hcfrevival.arena;

import com.google.common.collect.Maps;
import gg.hcfactions.libs.acf.PaperCommandManager;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import gg.hcfactions.libs.bukkit.services.impl.punishments.PunishmentService;
import lombok.Getter;
import net.hcfrevival.arena.command.ArenaCommand;
import net.hcfrevival.arena.items.*;
import net.hcfrevival.arena.listener.LobbyListener;
import net.hcfrevival.arena.listener.PlayerDataListener;
import net.hcfrevival.arena.listener.QueueListener;
import net.hcfrevival.arena.player.PlayerManager;
import net.hcfrevival.arena.queue.QueueManager;

import java.util.Map;

public final class ArenaPlugin extends AresPlugin {
    @Getter public Map<Class<? extends ArenaManager>, ArenaManager> managers;

    /*
        PLAYER STATES
            - LOBBY
            - LOBBY_INQUEUE
            - LOBBY_INPARTY
            - INGAME
            - SPECTATE_DEAD
            - SPECTATE

        QUEUE FLOW
            PLAYER JOIN
                GIVE QUEUE ITEMS
            RIGHT-CLICK QUEUE ITEM
                OPEN KIT SELECT GUI
            ENTER QUEUE
            UNRANKED
                SEARCH ANY PLAYER IN SAME QUEUE
            RANKED
                CREATE QUEUE OBJECT WITH CURRENT RATING + RANGE
            FIND MATCH
                CREATE SESSION
            TELEPORT TO ARENA
            START SESSION
            END SESSION, CONCLUDE RESULTS

        DUEL REQUEST FLOW
            TYPE COMMAND
            SELECT KIT
            OTHER PLAYER ACCEPT
            CREATE SESSION
            TELEPORT TO ARENA
            START SESSION
            END SESSION, CONCLUDE RESULTS

        PARTY/TEAM FLOW
            PLAYER JOIN
                GIVE TEAM ITEMS
            RIGHT-CLICK CREATE PARTY ITEM
            GIVE PARTY ITEM SET
            OPEN OTHER PARTIES GUI
            CLICK OTHER PARTY
            SELECT KIT
            ACCEPT DUEL REQUEST
            CREATE SESSION
            TELEPORT TO ARENA
            START SESSION
            END SESSION, CONCLUDE RESULTS
     */

    @Override
    public void onEnable() {
        super.onEnable();

        // services
        // custom item service
        final CustomItemService cis = new CustomItemService(this);
        cis.registerNewItem(new UnrankedQueueItem(this));
        cis.registerNewItem(new RankedQueueItem(this));
        cis.registerNewItem(new LeaveQueueItem(this));
        cis.registerNewItem(new EditKitItem());
        cis.registerNewItem(new CreatePartyItem());
        registerService(cis);

        startServices();

        // commands
        registerCommand(new ArenaCommand(this));

        // managers
        registerManager(new PlayerManager(this));
        registerManager(new QueueManager(this));
        managers.values().forEach(ArenaManager::onEnable);

        // listeners
        registerListener(new LobbyListener(this));
        registerListener(new PlayerDataListener(this));
        registerListener(new QueueListener(this));
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
