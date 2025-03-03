package com.thedtvn.login_Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;


public class Player_Event implements Listener {

    public static Login_Plugin root_plugin;

    public Player_Event(Login_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    public static void setLoginStatus(Player player, boolean status) {
        PersistentDataContainer pdc_player = player.getPersistentDataContainer();
        pdc_player.set(root_plugin.logined, PersistentDataType.BOOLEAN, status);
    }

    public static boolean getLoginStatus(Player player) {
        PersistentDataContainer pdc_player = player.getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc_player.get(root_plugin.logined, PersistentDataType.BOOLEAN));
    }

    public static void teleportPlayer(Player player, Location location) {
        final Runnable[] task = {null};
        task[0] = () -> {
            try {
                player.teleportAsync(location);
            } catch (RuntimeException e) {
                try {
                    player.teleport(location);
                } catch (RuntimeException ex) {
                    Scheduler.runLater(task[0], 20);
                }
            }
        };
        Scheduler.run(task[0]);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Server server = root_plugin.getServer();
        Player player = event.getPlayer();

        player.setGameMode(GameMode.SPECTATOR);
        player.setFlying(true);
        World world = server.getWorld("world");
        assert world != null;
        Location spawn_location = world.getSpawnLocation();
        Player_Event.teleportPlayer(player, spawn_location);

        setLoginStatus(player, false);

        YamlConfiguration config = root_plugin.getConfig("player_data.yml");
        String player_name = player.getName();
        boolean is_registered = config.contains(player_name);
        long join_time = System.currentTimeMillis();
        final Scheduler.Task[] login_task = {null};
        Runnable task = () -> {
            if (getLoginStatus(player)) {
                login_task[0].cancel();
                return;
            } else if (System.currentTimeMillis() - join_time > 60 * 1000) {
                login_task[0].cancel();
                Component kick_message = Component.text("Bạn đã bị kick bởi vì quá thời gian đăng nhập");
                player.kick(kick_message);
                return;
            }
            if (!is_registered) {
                player.sendMessage("Bạn chưa đăng ký. Hãy đăng ký với /reg <password>");
                Component bigtext = Component.text("Đăng ký với commands");
                Component subtext = Component.text("/reg <password>");
                Title text = Title.title(bigtext, subtext);
                player.showTitle(text);
            } else {
                Component bigtext = Component.text("Đăng nhâp với commands");
                Component subtext = Component.text("/log <password>");
                Title text = Title.title(bigtext, subtext);
                player.showTitle(text);
                player.sendMessage("Hãy đăng nhập với /login <password>");
            }
            teleportPlayer(player, spawn_location);
        };
        long delay = Scheduler.secToTick(3);
        login_task[0] = Scheduler.runTimer(task, 0, delay);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location player_location = player.getLocation();
        YamlConfiguration config = root_plugin.getConfig("player_data.yml");
        if (!getLoginStatus(player)) {
            return;
        }
        String player_name = player.getName();
        String world = player_location.getWorld().getName();
        HashMap<String, Object> player_data = new HashMap<>();
        player_data.put("world", world);
        player_data.put("x", player_location.getX());
        player_data.put("y", player_location.getY());
        player_data.put("z", player_location.getZ());
        player_data.put("game_mode", player.getGameMode().getValue());
        for (String key : player_data.keySet()) {
            config.set(player_name + "." + key, player_data.get(key));
        }
        System.out.println("Player quit: " + player_name);
        root_plugin.saveConfig(config, "player_data.yml");
    }

}
