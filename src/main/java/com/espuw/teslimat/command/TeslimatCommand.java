package com.espuw.teslimat.command;

import com.espuw.teslimat.TeslimatPlugin;
import com.espuw.teslimat.gui.AnaMenu;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeslimatCommand implements CommandExecutor, TabCompleter {

    private final TeslimatPlugin plugin;
    private final AnaMenu anaMenu;

    public TeslimatCommand(TeslimatPlugin plugin) {
        this.plugin = plugin;
        this.anaMenu = new AnaMenu(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("teslimat.teslimat")) {
            if (sender instanceof Player p) {
                plugin.getMessageUtil().send(p, "izin-yok");
            } else {
                sender.sendMessage("Bu komutu kullanmak için izniniz yok!");
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("teslimat.reload")) {
                if (sender instanceof Player p) {
                    plugin.getMessageUtil().send(p, "izin-yok");
                } else {
                    sender.sendMessage("Bu komutu kullanmak için izniniz yok!");
                }
                return true;
            }

            try {
                plugin.reload();
                if (sender instanceof Player p) {
                    plugin.getMessageUtil().send(p, "reload-tamam");
                } else {
                    sender.sendMessage("Konfigürasyon başarıyla yenilendi.");
                }
            } catch (ConfigManager.ConfigException | MessageUtil.MessageException e) {
                sender.sendMessage("§cReload sırasında hata oluştu: " + e.getMessage());
                plugin.getLogger().severe("Reload Hatası: " + e.getMessage());
            } catch (Exception e) {
                sender.sendMessage("§cBeklenmedik bir hata oluştu. Konsola bakın.");
                plugin.getLogger().severe("Beklenmedik Reload Hatası: " + e.getMessage());
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Sadece oyuncular menüyü açabilir.");
            return true;
        }

        player.openInventory(anaMenu.build(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("teslimat.reload")) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }
        return completions;
    }
}
