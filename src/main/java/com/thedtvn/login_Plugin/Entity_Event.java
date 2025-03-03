package com.thedtvn.login_Plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;


public class Entity_Event implements Listener {

    public static Login_Plugin root_plugin;

    public Entity_Event(Login_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    public static void setHealth(Entity entity) {
        if (entity instanceof Player) {
            return;
        }

        if (!(entity instanceof LivingEntity entityLiving)) {
            return;
        }
        int health = (int) entityLiving.getHealth();
        int maxHealth = (int) entityLiving.getMaxHealth();
        Component customName = Component.text( ChatColor.RED.toString() + health + " / " + maxHealth  + " ❤");
        entity.customName(customName);
        entity.setCustomNameVisible(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        setHealth(entity);
    }

    @EventHandler
    public void  onEntityRegain(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        setHealth(entity);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        setHealth(entity);
    }

}
