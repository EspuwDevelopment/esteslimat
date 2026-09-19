package com.espuw.teslimat.manager;

import com.espuw.teslimat.TeslimatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class AsamaManager {

    private static final int STAGE_COUNT = 5;
    private final TeslimatPlugin plugin;

    public AsamaManager(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean asamaTamamlandi(Player player, int asama) {
        long total = plugin.getTeslimatManager().getToplam(player.getUniqueId());
        return total >= plugin.getConfigManager().getAsamaGereksinim(asama);
    }

    public boolean odulAlindi(Player player, int asama) {
        Set<Integer> claimed = plugin.getDatabaseManager().getClaimed(player.getUniqueId());
        return claimed.contains(asama);
    }

    public boolean odulVer(Player player, int asama) {
        if (!asamaTamamlandi(player, asama)) return false;
        if (odulAlindi(player, asama)) return false;

        List<String> commands = plugin.getConfigManager().getAsamaOdulKomutlari(asama);
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    cmd.replace("%player%", player.getName()));
        }

        plugin.getDatabaseManager().addClaimed(player.getUniqueId(), asama);
        return true;
    }

    public int getStageCount() { return STAGE_COUNT; }
}
