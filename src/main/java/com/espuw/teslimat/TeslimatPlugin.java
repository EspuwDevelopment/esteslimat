package com.espuw.teslimat;

import com.espuw.teslimat.command.TeslimatCommand;
import com.espuw.teslimat.gui.GuiListener;
import com.espuw.teslimat.manager.AsamaManager;
import com.espuw.teslimat.manager.ConfigManager;
import com.espuw.teslimat.manager.DatabaseManager;
import com.espuw.teslimat.manager.TeslimatManager;
import com.espuw.teslimat.placeholder.TeslimatExpansion;
import com.espuw.teslimat.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeslimatPlugin extends JavaPlugin {

    private static TeslimatPlugin instance;

    private ConfigManager configManager;
    private MessageUtil messageUtil;
    private DatabaseManager databaseManager;
    private TeslimatManager teslimatManager;
    private AsamaManager asamaManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            saveDefaultConfig();
            saveResource("messages.yml", false);

            configManager = new ConfigManager(this);
            configManager.validate();

            messageUtil = new MessageUtil(this);
            messageUtil.validate();

            databaseManager = new DatabaseManager(this);
            databaseManager.init();

            teslimatManager = new TeslimatManager(this);
            asamaManager = new AsamaManager(this);

            TeslimatCommand cmd = new TeslimatCommand(this);
            getCommand("teslimat").setExecutor(cmd);
            getCommand("teslimat").setTabCompleter(cmd);

            getServer().getPluginManager().registerEvents(new GuiListener(this), this);

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new TeslimatExpansion(this).register();
            }

            getLogger().info("Espuw Teslimat Başarıyla Aktif Hale Getirildi! discord.gg/mcdev");

        } catch (ConfigManager.ConfigException e) {
            getLogger().severe("Espuw Teslimat hata verdi! Plugin kapatılıyor...");
            getLogger().severe("config.yml de hata var: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        } catch (MessageUtil.MessageException e) {
            getLogger().severe("Espuw Teslimat hata verdi! Plugin kapatılıyor...");
            getLogger().severe("messages.yml de hata var: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        } catch (Exception e) {
            getLogger().severe("Espuw Teslimat hata verdi! Plugin kapatılıyor...");
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    public void reload() throws ConfigManager.ConfigException, MessageUtil.MessageException {
        reloadConfig();
        configManager.reload();
        configManager.validate();
        messageUtil.reload();
        messageUtil.validate();
    }

    public static TeslimatPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager()     { return configManager; }
    public MessageUtil getMessageUtil()         { return messageUtil; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public TeslimatManager getTeslimatManager() { return teslimatManager; }
    public AsamaManager getAsamaManager()       { return asamaManager; }
}
