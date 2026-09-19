package com.espuw.teslimat.manager;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.util.ItemResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeslimatManager {

    private final TeslimatPlugin plugin;

    public TeslimatManager(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public int teslimEt(Player player) {
        String configItem = plugin.getConfigManager().getTeslimatItem();
        int total = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && ItemResolver.matches(item, configItem)) {
                total += item.getAmount();
                contents[i] = null;
            }
        }

        if (total == 0) return 0;

        player.getInventory().setContents(contents);
        player.updateInventory();
        plugin.getDatabaseManager().addTotal(player.getUniqueId(), total);
        return total;
    }

    public long getToplam(UUID uuid) {
        return plugin.getDatabaseManager().getTotal(uuid);
    }

    public List<Map.Entry<UUID, Long>> getTopList() {
        return plugin.getDatabaseManager().getTopList();
    }

    public String getTopOyuncuIsmi(int sira) {
        List<Map.Entry<UUID, Long>> top = getTopList();
        if (sira < 1 || sira > top.size()) return null;
        OfflinePlayer op = Bukkit.getOfflinePlayer(top.get(sira - 1).getKey());
        return op.getName();
    }

    public long getTopMiktar(int sira) {
        List<Map.Entry<UUID, Long>> top = getTopList();
        if (sira < 1 || sira > top.size()) return 0;
        return top.get(sira - 1).getValue();
    }
}
