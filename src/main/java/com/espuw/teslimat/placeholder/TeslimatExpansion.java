package com.espuw.teslimat.placeholder;

import com.espuw.teslimat.TeslimatPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PlaceholderAPI expansion.
 *
 * Kullanılabilir yer tutucular:
 *   %teslimat_toplam%      → oyuncunun toplam teslim sayısı
 *   %teslimat_top1%        → 1. sıradaki oyuncunun teslim sayısı
 *   %teslimat_top1_isim%   → 1. sıradaki oyuncunun ismi
 *   ... (top2, top3 ... top10)
 */
public class TeslimatExpansion extends PlaceholderExpansion {

    private final TeslimatPlugin plugin;

    public TeslimatExpansion(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "teslimat"; }

    @Override
    public @NotNull String getAuthor() { return "espuwnetwork"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        // %teslimat_toplam%
        if (params.equalsIgnoreCase("toplam")) {
            if (player == null) return "0";
            return String.valueOf(plugin.getTeslimatManager().getToplam(player.getUniqueId()));
        }

        // %teslimat_top{n}% — n. sıradaki miktar
        if (params.startsWith("top") && !params.endsWith("_isim")) {
            try {
                int sira = Integer.parseInt(params.substring(3));
                return String.valueOf(plugin.getTeslimatManager().getTopMiktar(sira));
            } catch (NumberFormatException ignored) {}
        }

        // %teslimat_top{n}_isim% — n. sıradaki oyuncu ismi
        if (params.startsWith("top") && params.endsWith("_isim")) {
            try {
                String numStr = params.substring(3, params.length() - 5);
                int sira = Integer.parseInt(numStr);
                String isim = plugin.getTeslimatManager().getTopOyuncuIsmi(sira);
                return isim != null ? isim : "-";
            } catch (NumberFormatException ignored) {}
        }

        return null;
    }
}
