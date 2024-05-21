package net.hcfrevival.arena.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.consumer.Promise;
import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.bukkit.builder.impl.ItemBuilder;
import gg.hcfactions.libs.bukkit.menu.IMenu;
import gg.hcfactions.libs.bukkit.menu.IMenuUpdater;
import gg.hcfactions.libs.bukkit.menu.impl.Clickable;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.arena.ArenaPlugin;
import net.hcfrevival.arena.gamerule.EGamerule;
import net.hcfrevival.arena.kit.KitManager;
import net.hcfrevival.arena.kit.impl.PlayerKit;
import net.hcfrevival.arena.util.KitFilterUtil;
import net.hcfrevival.arena.util.LobbyUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class KitEditorMenu implements IMenu {
    @Getter public ArenaPlugin plugin;
    @Getter public final Player player;
    @Getter public final Inventory inventory;
    @Getter public final EGamerule gamerule;
    @Getter public final Set<Clickable> items;
    @Getter public final Map<BukkitTask, IMenuUpdater> updateTasks;

    public KitEditorMenu(ArenaPlugin plugin, Player player, EGamerule gamerule) {
        String inventoryName = Strings.capitalize(gamerule.name().toLowerCase().replaceAll("_", " "));

        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 5 * 9, Component.text(inventoryName).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE));
        this.gamerule = gamerule;
        this.items = Sets.newConcurrentHashSet();
        this.updateTasks = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void open() {
        KitManager kitManager = (KitManager) plugin.getManagers().get(KitManager.class);

        List<ItemStack> uniqueItems = Lists.newArrayList();
        kitManager.getDefaultKit(gamerule).ifPresentOrElse(defaultKit -> {
            for (ItemStack item : defaultKit.getContents()) {
                if (item == null) {
                    continue;
                }

                String itemName = item.getType().name();
                if (itemName.endsWith("_HELMET") || itemName.endsWith("_CHESTPLATE") || itemName.endsWith("_LEGGINGS") || itemName.endsWith("_BOOTS")) {
                    continue;
                }

                if (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || item.getType().equals(Material.LINGERING_POTION)) {
                    PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

                    if (uniqueItems.stream().anyMatch(processedItem ->
                            processedItem.getItemMeta() != null
                            && processedItem.getType().equals(item.getType())
                            && ((PotionMeta)processedItem.getItemMeta()).getBasePotionType().equals(potionMeta.getBasePotionType()))) {

                        continue;
                    }

                    uniqueItems.add(item);
                    continue;
                }

                if (uniqueItems.stream().anyMatch(i -> i.getType().equals(item.getType()))) {
                    continue;
                }

                uniqueItems.add(item);
            }

        }, () -> {
            player.sendMessage(Component.text("This kit has not been configured", NamedTextColor.RED));
            new Scheduler(plugin).sync(player::closeInventory).delay(1L).run();
        });

        kitManager.getPlayerKit(player, gamerule).ifPresentOrElse(playerKit ->
                playerKit.apply(player, false),
        () -> kitManager.getDefaultKit(gamerule).ifPresent(defaultKit ->
                defaultKit.apply(player, false)));

        addItem(new Clickable(
                new ItemBuilder().setMaterial(Material.GREEN_CONCRETE)
                        .setName(Component.text("Save", NamedTextColor.GREEN))
                        .build(),

                10,

                click -> {
                    final PlayerKit playerKit = new PlayerKit(
                            player.getUniqueId(),
                            gamerule,
                            Arrays.asList(player.getInventory().getContents()),
                            Arrays.asList(player.getInventory().getArmorContents())
                    );

                    KitFilterUtil.isValid(Arrays.asList(player.getInventory().getContents()), gamerule, new Promise() {
                        @Override
                        public void resolve() {
                            player.sendMessage(Component.text("Your kit passed ranked play validation"));
                        }

                        @Override
                        public void reject(String s) {
                            player.sendMessage(Component.text("Your kit failed ranked play validation: " + s, NamedTextColor.RED));
                            player.sendMessage(Component.text("This kit can still be used in duels and unranked play", NamedTextColor.GRAY));
                        }
                    });

                    kitManager.savePlayerKit(player, playerKit);

                    player.closeInventory();
                    player.sendMessage(Component.text("Kit Saved", NamedTextColor.GREEN));
                }
        ));

        addItem(new Clickable(
                new ItemBuilder().setMaterial(Material.YELLOW_CONCRETE)
                        .setName(Component.text("Reset", NamedTextColor.YELLOW))
                        .build(),

                13,

                click -> {
                    kitManager.getDefaultKit(gamerule).ifPresent(defaultKit -> defaultKit.apply(player, false));
                }
        ));

        addItem(new Clickable(
                new ItemBuilder().setMaterial(Material.RED_CONCRETE)
                        .setName(Component.text("Delete Custom Kit", NamedTextColor.YELLOW))
                        .build(),

                16,

                click -> {
                    kitManager.deletePlayerKit(player, gamerule);
                    player.closeInventory();
                    player.sendMessage(Component.text("Kit Deleted", NamedTextColor.RED));
                }
        ));

        for (int i = 0; i < uniqueItems.size(); i++) {
            final int pos = 27 + i;
            final ItemStack item = uniqueItems.get(i);

            addItem(new Clickable(
                    item,
                    pos,
                    click -> player.setItemOnCursor(item)
            ));
        }

        IMenu.super.open();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);

        LobbyUtil.giveLobbyItems(plugin, player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (event.isShiftClick() || event.getClick().isKeyboardClick()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(inventory)) {
            getItemAtPosition(event.getRawSlot()).ifPresent(clickable -> clickable.getResult().click(event.getClick()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        final Set<Integer> rawSlots = event.getRawSlots();

        if (rawSlots.stream().anyMatch(slot -> slot < event.getInventory().getSize())) {
            event.setCancelled(true);
        }
    }
}
