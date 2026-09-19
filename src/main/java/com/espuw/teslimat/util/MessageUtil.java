package com.espuw.teslimat.util;

import com.espuw.teslimat.TeslimatPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageUtil {

    public static class MessageException extends Exception {
        public MessageException(String msg) { super(msg); }
    }

    private final TeslimatPlugin plugin;
    private FileConfiguration cfg;

    public MessageUtil(TeslimatPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        File f = new File(plugin.getDataFolder(), "messages.yml");
        if (!f.exists()) plugin.saveResource("messages.yml", false);
        cfg = YamlConfiguration.loadConfiguration(f);
    }

    public void reload() {
        load();
    }

    public void validate() throws MessageException {
        if (cfg.getString("prefix") == null) {
            throw new MessageException("prefix anahtarı eksik.");
        }
        String[] required = {
            "teslim-edildi", "teslim-edilecek-yok", "odul-alindi",
            "asama-tamamlanmadi", "asama-zaten-alindi", "izin-yok", "reload-tamam"
        };
        for (String key : required) {
            if (cfg.getString(key) == null) {
                throw new MessageException(key + " anahtarı eksik.");
            }
        }
    }

    public String prefix() {
        return c(cfg.getString("prefix", "&6espuwnetwork &8»&r"));
    }

    public String get(String key, String... replacements) {
        String raw = cfg.getString(key, "&c[Eksik mesaj: " + key + "]");
        String msg = prefix() + " " + c(raw);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }

    public void send(Player player, String key, String... replacements) {
        player.sendMessage(get(key, replacements));
    }

    private static String c(String s) {
        return s == null ? "" : s.replace("&", "§");
    }
}
