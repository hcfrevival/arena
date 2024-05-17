package net.hcfrevival.arena;

import gg.hcfactions.libs.bukkit.location.impl.PLocatable;
import gg.hcfactions.libs.bukkit.utils.Configs;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ArenaConfig {
    @Getter public String mongoUri;
    @Getter public String mongoDatabaseName;
    @Getter public String redisUri;

    @Getter public PLocatable spawnLocation;
    @Getter public PLocatable kitEditorLocation;

    private final ArenaPlugin plugin;

    public ArenaConfig(ArenaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setSpawnLocation(PLocatable location) {
        final YamlConfiguration conf = plugin.loadConfiguration("config");
        Configs.writePlayerLocation(conf, "locations.spawn", location);
        plugin.saveConfiguration("config", conf);

        this.spawnLocation = location;
    }

    public void setKitEditorLocation(PLocatable location) {
        final YamlConfiguration conf = plugin.loadConfiguration("config");
        Configs.writePlayerLocation(conf, "locations.editor", location);
        plugin.saveConfiguration("config", conf);

        this.kitEditorLocation = location;
    }

    public void load() {
        final YamlConfiguration conf = plugin.loadConfiguration("config");

        this.mongoUri = conf.getString("db.mongo.uri");
        this.mongoDatabaseName = conf.getString("db.mongo.database");
        this.redisUri = conf.getString("db.redis.uri");

        this.spawnLocation = Configs.parsePlayerLocation(conf, "locations.spawn");
        this.kitEditorLocation = Configs.parsePlayerLocation(conf, "locations.editor");
    }
}
