package net.hcfrevival.arena.ranked;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import gg.hcfactions.libs.base.connect.impl.mongo.Mongo;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.ranked.impl.RankedPlayer;
import net.hcfrevival.arena.ranked.impl.RankedTeam;
import org.bson.Document;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
public final class RankedManager extends ArenaManager {
    public final Set<IRankedProfile> rankedProfileRepository;

    public RankedManager(ArenaPlugin plugin) {
        super(plugin);
        this.rankedProfileRepository = Sets.newConcurrentHashSet();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        final long start = Time.now();
        plugin.getAresLogger().warn("Saving Ranked Data...");
        rankedProfileRepository.forEach(profile -> saveProfile(profile, false));
        final long end = Time.now();
        final long diff = (end - start);

        plugin.getAresLogger().info("Finished saving Ranked Data. (took {}ms)", diff);
    }

    public boolean isProfileLoaded(UUID uuid) {
        return rankedProfileRepository.stream().anyMatch(rp -> rp.getUniqueId().equals(uuid));
    }

    public Optional<IRankedProfile> getProfile(UUID uuid) {
        return rankedProfileRepository.stream().filter(rp -> rp instanceof RankedPlayer && rp.getUniqueId().equals(uuid)).findFirst();
    }

    public IRankedProfile loadProfile(UUID uuid) {
        MongoDatabase db = ((Mongo) plugin.getConnectable(Mongo.class)).getDatabase(plugin.getConfiguration().getMongoDatabaseName());
        MongoCollection<Document> collection = db.getCollection(plugin.getConfiguration().getRankedProfileCollectionName());

        Document res = collection.find(Filters.eq("uuid", uuid.toString())).first();
        if (res == null) {
            plugin.getAresLogger().error("Failed to find document");
            return null;
        }

        if (res.containsKey("player")) {
            plugin.getAresLogger().info("Loaded RankedPlayer document");
            return new RankedPlayer(uuid).fromDocument(res);
        }

        plugin.getAresLogger().info("Loaded RankedTeam document");
        return new RankedTeam(uuid).fromDocument(res);
    }

    public void saveProfile(IRankedProfile profile, boolean async) {
        if (async) {
            new Scheduler(plugin).async(() -> {
                MongoDatabase db = ((Mongo) plugin.getConnectable(Mongo.class)).getDatabase(plugin.getConfiguration().getMongoDatabaseName());
                MongoCollection<Document> collection = db.getCollection(plugin.getConfiguration().getRankedProfileCollectionName());
                Document existing = collection.find(Filters.eq("uuid", profile.getUniqueId().toString())).first();

                if (existing != null) {
                    collection.replaceOne(existing, profile.toDocument());
                    return;
                }

                collection.insertOne(profile.toDocument());
            }).run();

            return;
        }

        MongoDatabase db = ((Mongo) plugin.getConnectable(Mongo.class)).getDatabase(plugin.getConfiguration().getMongoDatabaseName());
        MongoCollection<Document> collection = db.getCollection(plugin.getConfiguration().getRankedProfileCollectionName());
        Document existing = collection.find(Filters.eq("uuid", profile.getUniqueId().toString())).first();

        if (existing != null) {
            collection.replaceOne(existing, profile.toDocument());
            return;
        }

        collection.insertOne(profile.toDocument());
    }
}
