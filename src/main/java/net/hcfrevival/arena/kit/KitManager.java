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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class KitManager extends ArenaManager {
    @Getter public final Set<IArenaKit> kitRepository;

    /*
        player_kits/johnsama.yml
            data:
                NO_DEBUFF:
                    <data>
                DEBUFF:
                    <data>
        default_kits.yml
            NO_DEBUFF:
                <data>
            DEBUFF:
                <data>
     */

    public KitManager(ArenaPlugin plugin) {
        super(plugin);
        kitRepository = Sets.newConcurrentHashSet();
    }

    @Override
    public void onEnable() {
        loadDefaultKits();

        super.onEnable();
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

    public void loadDefaultKits() {
        final YamlConfiguration conf = plugin.loadConfiguration("default_kits");
        int loaded = 0;

        for (String gameruleName : conf.getConfigurationSection("data").getKeys(false)) {
            final EGamerule gamerule;
            try {
                gamerule = EGamerule.valueOf(gameruleName);
            } catch (IllegalArgumentException e) {
                plugin.getAresLogger().error("Invalid gamerule: " + gameruleName);
                continue;
            }

            final List<ItemStack> contents = (List<ItemStack>)conf.getList("data." + gameruleName + ".contents");
            final List<ItemStack> armor = (List<ItemStack>)conf.getList("data." + gameruleName + ".armor");
            final DefaultKit defaultKit = new DefaultKit(gamerule, contents, armor);
            kitRepository.add(defaultKit);

            loaded += 1;
        }

        plugin.getAresLogger().info("Loaded " + loaded + " Default Kits");
    }

    public void saveDefaultKit(DefaultKit kit) {
        kitRepository.removeIf(k -> k instanceof final DefaultKit defaultKit && defaultKit.getGamerule().equals(kit.getGamerule()));
        kitRepository.add(kit);

        final YamlConfiguration conf = plugin.loadConfiguration("default_kits");

        conf.set("data." + kit.getGamerule().name() + ".armor", kit.getArmorContents());
        conf.set("data." + kit.getGamerule().name() + ".contents", kit.getContents());

        plugin.saveConfiguration("default_kits", conf);
    }

    public void savePlayerKit(Player player, PlayerKit kit) {
        kitRepository.removeIf(k -> k instanceof final PlayerKit playerKit && playerKit.getGamerule().equals(kit.getGamerule()));
        kitRepository.add(kit);

        final YamlConfiguration conf = plugin.loadConfiguration(File.separator + "player_kits" + File.separator + player.getUniqueId().toString());

        conf.set("data." + kit.getGamerule().name() + ".armor", kit.getArmorContents());
        conf.set("data." + kit.getGamerule().name() + ".contents", kit.getContents());

        plugin.saveConfiguration(File.separator + "player_kits" + File.separator + player.getUniqueId().toString(), conf);
    }
}
