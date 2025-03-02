package com.thedtvn.login_Plugin;

import com.thedtvn.login_Plugin.commands.Login;
import com.thedtvn.login_Plugin.commands.Reg;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import java.io.File;
import java.util.HashMap;

public final class Login_Plugin extends JavaPlugin {

    public static Login_Plugin getInstance() {
        return getPlugin(Login_Plugin.class);
    }

    public NamespacedKey logined = new NamespacedKey(this, "logined");

    public HashMap<String, YamlConfiguration> cacheConfig = new HashMap<>();

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        getCommand("login").setExecutor(new Login(this));
        getCommand("reg").setExecutor(new Reg(this));
        pm.registerEvents(new Player_Event(this), this);
    }

    public File getConfigFileDir(String file_name) {
        return getDataFolder().toPath().resolve(file_name).toFile();
    }

    public YamlConfiguration getConfig(String file_name) {
        if (cacheConfig.containsKey(file_name)) {
            return cacheConfig.get(file_name);
        }
        File file = getConfigFileDir(file_name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(YamlConfiguration config, String file_name) {
        cacheConfig.put(file_name, config);
        try {
            config.save(getConfigFileDir(file_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        PluginManager pm = getServer().getPluginManager();
        pm.disablePlugin(this);
    }
}
