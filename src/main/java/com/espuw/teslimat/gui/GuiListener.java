package com.espuw.teslimat.gui;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiListener implements Listener {

    private final TeslimatPlugin plugin;
    private final AnaMenu        anaMenu;
    private final TeslimMenu     teslimMenu;
    private final SiralamaMenu   siralamaMenu;

    private final Map<UUID, Boolean> pendingOpen = new HashMap<>();

    public GuiListener(TeslimatPlugin plugin) {
        this.plugin       = plugin;
        this.anaMenu      = new AnaMenu(plugin);
        this.teslimMenu   = new TeslimMenu(plugin);
        this.siralamaMenu = new SiralamaMenu(plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof TeslimatHolder th)) return;

        event.setCancelled(true);

        if (event.getClickedInventory() != event.getInventory()) return;
        if (event.getCurrentItem() == null) return;

        String menuId = th.getMenuId();
        int slot = event.getSlot();

        switch (menuId) {
            case AnaMenu.ID     -> handleAnaMenu(player, slot);
            case TeslimMenu.ID  -> handleTeslimMenu(player, slot);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof TeslimatHolder th)) return;

        UUID uuid = player.getUniqueId();
        if (pendingOpen.getOrDefault(uuid, false)) return;

        pendingOpen.put(uuid, true);

        switch (th.getMenuId()) {
            case TeslimMenu.ID ->
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    pendingOpen.remove(uuid);
                    player.openInventory(anaMenu.build(player));
                }, 1L);

            case SiralamaMenu.ID ->
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    pendingOpen.remove(uuid);
                    player.openInventory(teslimMenu.build(player));
                }, 1L);

            default ->
                pendingOpen.remove(uuid);
        }
    }

    private void handleAnaMenu(Player player, int slot) {
        if (slot == 13) {
            openWithoutBackNav(player, () -> player.openInventory(teslimMenu.build(player)));
        }
    }

    private void handleTeslimMenu(Player player, int slot) {
        if (slot == 12) {
            openWithoutBackNav(player, () -> {
                int miktar = plugin.getTeslimatManager().teslimEt(player);
                MessageUtil msg = plugin.getMessageUtil();

                if (miktar == 0) {
                    msg.send(player, "teslim-edilecek-yok");
                } else {
                    long total = plugin.getTeslimatManager().getToplam(player.getUniqueId());
                    String itemAdi = ConfigManager.friendlyMaterial(plugin.getConfigManager().getTeslimatItem());
                    msg.send(player, "teslim-edildi",
                            "%miktar%", String.valueOf(miktar),
                            "%item%", itemAdi,
                            "%toplam%", String.valueOf(total));
                }
                player.openInventory(teslimMenu.build(player));
            });
            return;
        }

        if (slot == 14) {
            openWithoutBackNav(player, () -> player.openInventory(siralamaMenu.build(player)));
            return;
        }

        int[] asamaSlotlari = TeslimMenu.ASAMA_SLOTS;
        for (int i = 0; i < asamaSlotlari.length; i++) {
            if (slot == asamaSlotlari[i]) {
                handleAsamaTikla(player, i + 1);
                openWithoutBackNav(player, () -> player.openInventory(teslimMenu.build(player)));
                return;
            }
        }
    }

    private void handleAsamaTikla(Player player, int asama) {
        MessageUtil msg = plugin.getMessageUtil();

        if (!plugin.getAsamaManager().asamaTamamlandi(player, asama)) {
            msg.send(player, "asama-tamamlanmadi");
            return;
        }
        if (plugin.getAsamaManager().odulAlindi(player, asama)) {
            msg.send(player, "asama-zaten-alindi");
            return;
        }
        if (plugin.getAsamaManager().odulVer(player, asama)) {
            msg.send(player, "odul-alindi");
        }
    }

    private void openWithoutBackNav(Player player, Runnable openAction) {
        UUID uuid = player.getUniqueId();
        pendingOpen.put(uuid, true);
        player.closeInventory();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pendingOpen.remove(uuid);
            openAction.run();
        }, 1L);
    }
}
