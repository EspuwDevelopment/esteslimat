package com.espuw.teslimat.gui;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SiralamaMenu {

    public static final String ID = "siralama";

    private static final int[] SLOTS = {4, 12, 14, 20, 22, 24, 28, 30, 32, 34};

    private final TeslimatPlugin plugin;

    public SiralamaMenu(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        TeslimatHolder holder = new TeslimatHolder(ID);
        Inventory inv = Bukkit.createInventory(holder, 36, cfg.getSiralamaMenuIsmi());
        holder.setInventory(inv);

        fillGlass(inv, 36);

        List<Map.Entry<UUID, Long>> top = plugin.getTeslimatManager().getTopList();

        for (int i = 0; i < SLOTS.length; i++) {
            int slot = SLOTS[i];
            int rank = i + 1;

            if (i < top.size()) {
                UUID uuid   = top.get(i).getKey();
                long amount = top.get(i).getValue();
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                String name = op.getName() != null ? op.getName() : "Bilinmiyor";

                ItemStack skull;
                if (op.isOnline()) {
                    skull = ItemUtil.playerSkull(op.getPlayer());
                } else {
                    skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta sm = (SkullMeta) skull.getItemMeta();
                    if (sm != null) { sm.setOwningPlayer(op); skull.setItemMeta(sm); }
                }

                ItemMeta meta = skull.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§e" + rank + ". §f" + name);
                    meta.setLore(buildLore(cfg.getSiralamaDoluLore(), rank, name, amount));
                    skull.setItemMeta(meta);
                }
                inv.setItem(slot, skull);

            } else {
                ItemStack paper = new ItemStack(Material.PAPER);
                ItemMeta meta = paper.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§7" + rank + ". §8Boş");
                    meta.setLore(cfg.getSiralamaBosBLore());
                    paper.setItemMeta(meta);
                }
                inv.setItem(slot, paper);
            }
        }

        return inv;
    }

    private List<String> buildLore(List<String> tpl, int rank, String name, long amount) {
        List<String> out = new ArrayList<>();
        for (String s : tpl) {
            out.add(s
                .replace("%sira%",             String.valueOf(rank))
                .replace("%oyuncu%",           name)
                .replace("%teslimat_miktar%",  String.valueOf(amount))
            );
        }
        return out;
    }

    private void fillGlass(Inventory inv, int size) {
        ItemStack g = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = g.getItemMeta();
        if (m != null) { m.setDisplayName("§r"); g.setItemMeta(m); }
        for (int i = 0; i < size; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, g);
        }
    }
}
