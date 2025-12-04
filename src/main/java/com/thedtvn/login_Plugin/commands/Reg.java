package com.thedtvn.login_Plugin.commands;

import com.thedtvn.login_Plugin.Login_Plugin;
import com.thedtvn.login_Plugin.Player_Event;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import static com.thedtvn.login_Plugin.commands.Login.hashPassword;
import java.util.Objects;

public class Reg implements CommandExecutor {

    public static Login_Plugin root_plugin;

    public Reg(Login_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        YamlConfiguration config = root_plugin.getConfig("player_data.yml");
        String player_name = player.getName();
        boolean is_registered = config.contains(player_name);

        if (is_registered) {
            player.sendMessage("Bạn đã đăng ký.");
            return true;
        }

        if (Player_Event.getLoginStatus(player)) {
            player.sendMessage("Bạn đã đăng nhập.");
            return true;
        }

        if (split.length != 2) {
            player.sendMessage("Sử dụng: /reg <password> <password_confirm>");
            return true;
        }

        String password = split[0];
        String confirm_password = split[1];

        if (!Objects.equals(password, confirm_password)) {
            player.sendMessage("Xác nhận mật khẩu không chính xác.");
            return true;
        }

        config.set(player_name + ".password", hashPassword(password));

        root_plugin.saveConfig(config, "player_data.yml");

        Player_Event.setLoginStatus(player, true);
        player.setGameMode(GameMode.SURVIVAL);

        player.sendMessage("Bạn đã đăng kí thành công.");
        player.clearTitle();
        return true;
    }

}
