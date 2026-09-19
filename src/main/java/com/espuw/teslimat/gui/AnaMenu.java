package com.espuw.teslimat.gui;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.util.ItemResolver;
import com.espuw.teslimat.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AnaMenu {

    public static final String ID = "ana";

    private final TeslimatPlugin plugin;

    public AnaMenu(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        TeslimatHolder holder = new TeslimatHolder(ID);
        Inventory inv = Bukkit.createInventory(holder, 27, cfg.getAnaMenuIsmi());
        holder.setInventory(inv);

        fillGlass(inv, 27);

        ItemStack skull = ItemUtil.playerSkull(player);
        ItemMeta sm = skull.getItemMeta();
        if (sm != null) { sm.setDisplayName("§e" + player.getName()); skull.setItemMeta(sm); }
        inv.setItem(4, skull);

        long total = plugin.getTeslimatManager().getToplam(player.getUniqueId());
        ItemStack ti = ItemResolver.resolve(cfg.getTeslimatItem());
        ItemMeta tm = ti.getItemMeta();
        if (tm != null) {
            tm.setDisplayName("§e" + ConfigManager.friendlyMaterial(cfg.getTeslimatItem()));
            tm.setLore(applyLore(cfg.getAnaMenuTeslimatLore(), cfg, total));
            ti.setItemMeta(tm);
        }
        inv.setItem(13, ti);

        return inv;
    }

    private List<String> applyLore(List<String> tpl, ConfigManager cfg, long total) {
        List<String> out = new ArrayList<>();
        for (String line : tpl) {
            out.add(line
                .replace("%sezon_bilgisi%", cfg.getSezonBilgisi())
                .replace("%sure%",          cfg.getSure())
                .replace("%item_adi%",      ConfigManager.friendlyMaterial(cfg.getTeslimatItem()))
                .replace("%teslimat_toplam%", String.valueOf(total))
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
