package com.espuw.teslimat.gui;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.util.ItemResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeslimMenu {

    public static final String ID = "teslim";
    public static final int[] ASAMA_SLOTS = {20, 21, 22, 23, 24};

    private final TeslimatPlugin plugin;

    public TeslimMenu(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        TeslimatHolder holder = new TeslimatHolder(ID);
        Inventory inv = Bukkit.createInventory(holder, 36, cfg.getTeslimMenuIsmi());
        holder.setInventory(inv);

        fillGlass(inv, 36);

        long total = plugin.getTeslimatManager().getToplam(player.getUniqueId());

        ItemStack ti = ItemResolver.buildSkull(ItemResolver.GEZGIN_KOYLU);
        ItemMeta tm = ti.getItemMeta();
        if (tm != null) {
            tm.setDisplayName("§aTeslim Et");
            tm.setLore(applyTotalLore(cfg.getTeslimEtLore(), total));
            ti.setItemMeta(tm);
        }
        inv.setItem(12, ti);

        ItemStack ii = ItemResolver.buildSkull(ItemResolver.ELMAS_KASKLI_STEVE);
        ItemMeta im = ii.getItemMeta();
        if (im != null) {
            im.setDisplayName("§6İlk 10");
            im.setLore(cfg.getIlk10Lore());
            ii.setItemMeta(im);
        }
        inv.setItem(14, ii);

        for (int i = 0; i < ASAMA_SLOTS.length; i++) {
            inv.setItem(ASAMA_SLOTS[i], buildStage(player, i + 1, cfg));
        }

        return inv;
    }

    private ItemStack buildStage(Player player, int stage, ConfigManager cfg) {
        boolean done    = plugin.getAsamaManager().asamaTamamlandi(player, stage);
        boolean claimed = plugin.getAsamaManager().odulAlindi(player, stage);

        ItemStack skull = ItemResolver.buildSkull(ItemResolver.X_ISARETI);
        ItemMeta meta = skull.getItemMeta();
        if (meta == null) return skull;

        meta.setDisplayName("§e" + stage + ". Aşama");

        List<String> lore = new ArrayList<>();
        for (String s : cfg.getAsamaGereksinimLore()) {
            lore.add(s
                .replace("%gereksinim%", String.valueOf(cfg.getAsamaGereksinim(stage)))
                .replace("%item_adi%",   ConfigManager.friendlyMaterial(cfg.getTeslimatItem()))
            );
        }
        lore.addAll(cfg.getAsamaLore(stage));

        if (claimed)    lore.addAll(cfg.getAsamaOdulAlindiEk());
        else if (done)  lore.addAll(cfg.getAsamaTamamlandiEk());
        else            lore.addAll(cfg.getAsamaTamamlanmadiEk());

        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

    private List<String> applyTotalLore(List<String> tpl, long total) {
        List<String> out = new ArrayList<>();
        for (String s : tpl) out.add(s.replace("%teslimat_toplam%", String.valueOf(total)));
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
