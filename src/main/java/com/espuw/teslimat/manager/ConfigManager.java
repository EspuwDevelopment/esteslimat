package com.espuw.teslimat.manager;

import com.espuw.teslimat.TeslimatPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    public static class ConfigException extends Exception {
        public ConfigException(String msg) { super(msg); }
    }

    private final TeslimatPlugin plugin;

    public ConfigManager(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public void validate() throws ConfigException {
        String item = cfg().getString("teslimat-item");
        if (item == null || item.isBlank()) {
            throw new ConfigException("teslimat-item boş olamaz.");
        }
        for (int i = 1; i <= 5; i++) {
            int req = cfg().getInt("asamalar.asama" + i + ".gereksinim", -1);
            if (req < 0) {
                throw new ConfigException("asamalar.asama" + i + ".gereksinim gecersiz veya eksik.");
            }
        }
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public String getTeslimatItem() {
        return cfg().getString("teslimat-item", "WHEAT");
    }

    public String getSezonBilgisi() {
        return c(cfg().getString("sezon-bilgisi", "Sezon 1"));
    }

    public String getSure() {
        return c(cfg().getString("sure", "Sezon sonuna kadar"));
    }

    public String getAnaMenuIsmi() {
        return cfg().getString("menus.ana-menu", "Teslimat");
    }

    public String getTeslimMenuIsmi() {
        return cfg().getString("menus.teslim-menu", "Teslimat Menüsü");
    }

    public String getSiralamaMenuIsmi() {
        return cfg().getString("menus.siralama-menu", "İlk 10");
    }

    public List<String> getAnaMenuTeslimatLore() {
        return cl(cfg().getStringList("ana-menu-teslimat-lore"));
    }

    public List<String> getTeslimEtLore() {
        return cl(cfg().getStringList("teslim-et-lore"));
    }

    public List<String> getIlk10Lore() {
        return cl(cfg().getStringList("ilk10-lore"));
    }

    public List<String> getSiralamaDoluLore() {
        return cl(cfg().getStringList("siralama-dolu-lore"));
    }

    public List<String> getSiralamaBosBLore() {
        return cl(cfg().getStringList("siralama-bos-lore"));
    }

    public List<String> getAsamaGereksinimLore() {
        return cl(cfg().getStringList("asama-gereksinim-lore"));
    }

    public List<String> getAsamaTamamlandiEk() {
        return cl(cfg().getStringList("asama-tamamlandi-ek"));
    }

    public List<String> getAsamaOdulAlindiEk() {
        return cl(cfg().getStringList("asama-odul-alindi-ek"));
    }

    public List<String> getAsamaTamamlanmadiEk() {
        return cl(cfg().getStringList("asama-tamamlanmadi-ek"));
    }

    public int getAsamaGereksinim(int asama) {
        return cfg().getInt("asamalar.asama" + asama + ".gereksinim", 0);
    }

    public List<String> getAsamaOdulKomutlari(int asama) {
        return cfg().getStringList("asamalar.asama" + asama + ".odul-komutlari");
    }

    public List<String> getAsamaLore(int asama) {
        return cl(cfg().getStringList("asamalar.asama" + asama + ".lore"));
    }

    public static String c(String s) {
        return s == null ? "" : s.replace("&", "§");
    }

    public static List<String> cl(List<String> list) {
        return list.stream().map(ConfigManager::c).toList();
    }

    public static String friendlyMaterial(String raw) {
        if (raw == null) return "";
        String upper = raw.toUpperCase();
        return switch (upper) {
            case "WHEAT"          -> "Buğday";
            case "CARROT"         -> "Havuç";
            case "POTATO"         -> "Patates";
            case "BEETROOT"       -> "Pancar";
            case "SUGAR_CANE"     -> "Şeker Kamışı";
            case "MELON"          -> "Karpuz";
            case "PUMPKIN"        -> "Kabak";
            case "APPLE"          -> "Elma";
            case "BREAD"          -> "Ekmek";
            case "COAL"           -> "Kömür";
            case "IRON_INGOT"     -> "Demir Külçesi";
            case "GOLD_INGOT"     -> "Altın Külçesi";
            case "DIAMOND"        -> "Elmas";
            case "PAPER"          -> "Kağıt";
            default -> {
                if (raw.toLowerCase().startsWith("head-")) yield "Özel Kafa";
                if (raw.toLowerCase().startsWith("ia:"))   yield raw.substring(3);
                yield upper.charAt(0) + upper.substring(1).toLowerCase().replace("_", " ");
            }
        };
    }
}
