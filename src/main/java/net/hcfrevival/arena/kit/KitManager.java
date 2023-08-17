package net.hcfrevival.arena.kit;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.services.impl.items.CustomItemService;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.items.CustomKitBook;
import net.hcfrevival.arena.items.DefaultKitBook;
import net.hcfrevival.arena.kit.impl.DefaultKit;
import net.hcfrevival.arena.kit.impl.PlayerKit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class KitManager extends ArenaManager {
    @Getter public final Set<IArenaKit> kitRepository;

    public KitManager(ArenaPlugin plugin) {
        super(plugin);
        kitRepository = Sets.newConcurrentHashSet();
    }

    public List<PlayerKit> getPlayerKits() {
        final List<PlayerKit> res = Lists.newArrayList();

        kitRepository.stream().filter(k -> k instanceof PlayerKit).forEach(playerKit -> {
            res.add(((PlayerKit) playerKit));
        });

        return res;
    }

    public Optional<PlayerKit> getPlayerKit(Player player, EGamerule gamerule) {
        return getPlayerKits().stream().filter(k -> k.getOwnerId().equals(player.getUniqueId()) && k.getGamerule().equals(gamerule)).findFirst();
    }

    public Optional<DefaultKit> getDefaultKit(EGamerule gamerule) {
        final Optional<IArenaKit> kitQuery = kitRepository.stream().filter(k -> k instanceof final DefaultKit defaultKit && defaultKit.getGamerule().equals(gamerule)).findFirst();

        if (kitQuery.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((DefaultKit) kitQuery.get());
    }

    public void giveKitBooks(Player player, EGamerule gamerule) {
        final CustomItemService cis = (CustomItemService) plugin.getService(CustomItemService.class);
        getDefaultKit(gamerule).flatMap(defaultKit -> cis.getItem(DefaultKitBook.class)).ifPresent(defaultKitItem -> player.getInventory().addItem(defaultKitItem.getItem()));
        getPlayerKit(player, gamerule).flatMap(playerKit -> cis.getItem(CustomKitBook.class)).ifPresent(playerKitItem -> player.getInventory().addItem(playerKitItem.getItem()));
    }
}
