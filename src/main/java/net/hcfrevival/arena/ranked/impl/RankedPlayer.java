package net.hcfrevival.arena.ranked.impl;

import com.google.common.collect.Maps;
import gg.hcfactions.libs.base.connect.impl.mongo.MongoDocument;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.ranked.IRankedProfile;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

@Getter
public final class RankedPlayer implements IRankedProfile, MongoDocument {
    public UUID uniqueId;
    public final Map<EGamerule, Integer> ratings;

    public RankedPlayer(UUID uuid) {
        this.uniqueId = uuid;
        this.ratings = Maps.newHashMap();
    }

    @Override
    public RankedPlayer fromDocument(Document document) {
        this.uniqueId = UUID.fromString(document.getString("uuid"));

        Document valuesDoc = document.get("gamerules", Document.class);
        for (EGamerule gamerule : EGamerule.values()) {
            if (!valuesDoc.containsKey(gamerule.name().toLowerCase())) {
                continue;
            }

            this.getRatings().put(gamerule, valuesDoc.getInteger(gamerule.name().toLowerCase()));
        }

        return this;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        Document valueDoc = new Document();

        doc.put("uuid", uniqueId.toString());
        doc.put("player", true);

        for (EGamerule gamerule : EGamerule.values()) {
            valueDoc.put(gamerule.name().toLowerCase(), ratings.get(gamerule));
        }

        doc.put("gamerules", valueDoc);

        return doc;
    }
}
