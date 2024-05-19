package net.hcfrevival.arena.ranked.impl;

import com.google.common.collect.Maps;
import gg.hcfactions.libs.base.connect.impl.mongo.MongoDocument;
import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.ranked.IRankedProfile;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

public class RankedTeam implements IRankedProfile, MongoDocument {
    @Getter UUID uniqueId;
    @Getter public final Map<EGamerule, Integer> ratings;

    public RankedTeam(UUID uuid) {
        this.uniqueId = uuid;
        this.ratings = Maps.newHashMap();
    }

    @Override
    public RankedTeam fromDocument(Document document) {
        return null;
    }

    @Override
    public Document toDocument() {
        return null;
    }
}
