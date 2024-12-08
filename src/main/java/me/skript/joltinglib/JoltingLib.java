package me.skript.joltinglib;

import me.skript.joltinglib.colorcodes.JText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoltingLib extends JavaPlugin implements Listener {

    private static JoltingLib instance;

    @Override
    public void onEnable() {
        instance = this;

        // Register the block break event
        Bukkit.getPluginManager().registerEvents(this, this);

        getServer().getConsoleSender().sendMessage(JText.format("&3&l[JoltingLib] &7Libraries have been enabled!"));
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(JText.format("&3&l[JoltingLib] &7Libraries have been disabled!"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Send a message to the player when they break a block
        player.sendMessage("You broke a " + event.getBlock().getType().name());

        if(player.isSneaking()) {
            event.setCancelled(true);
        }
    }

    public static JoltingLib getInstance() {
        return instance;
    }
}
