package com.thedtvn.login_Plugin.commands;

import com.thedtvn.login_Plugin.Login_Plugin;
import com.thedtvn.login_Plugin.Player_Event;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Login implements CommandExecutor {

    public static Login_Plugin root_plugin;

    public Login(Login_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashInBytes = md.digest(password.getBytes());

            // Convert byte array into signum representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        YamlConfiguration config = root_plugin.getConfig("player_data.yml");
        String player_name = player.getName();
        boolean is_registered = config.contains(player_name);

        if (!is_registered) {
            player.sendMessage("Bạn chưa đăng ký.");
            return true;
        }

        if (Player_Event.getLoginStatus(player)) {
            player.sendMessage("Bạn đã đăng nhập.");
            return true;
        }

        if (split.length != 1) {
            player.sendMessage("Sử dụng: /login <password>");
            return true;
        }

        String password = split[0];

        String saved_password = config.getString(player_name + ".password");
        if (!Objects.equals(saved_password, hashPassword(password))) {
            player.sendMessage("Mật khẩu không chính xác.");
            return true;
        }

        Player_Event.setLoginStatus(player, true);
        String world_name = config.getString(player_name + ".world");
        if (world_name != null) {
            long x = config.getLong(player_name + ".x");
            long y = config.getLong(player_name + ".y");
            long z = config.getLong(player_name + ".z");
            int game_mode = config.getInt(player_name + ".game_mode");
            World world = root_plugin.getServer().getWorld(world_name);
            Location location = new Location(world, x, y, z);
            player.setGameMode(GameMode.getByValue(game_mode));
            Player_Event.teleportPlayer(player, location);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }

        player.sendMessage("Bạn đã đăng nhập thành công.");
        player.clearTitle();
        return true;
    }

}
