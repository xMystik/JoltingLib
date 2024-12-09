package me.skript.joltinglib;

import me.skript.joltinglib.colorcodes.JText;
import me.skript.joltinglib.nbt.JNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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
    public void onBlockBreak(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItemStack = event.getItemDrop().getItemStack();

        // Send a message to the player when they break a block
        player.sendMessage("You threw a " + droppedItemStack.getType());

        if(JNBT.getDataContainer(droppedItemStack).isEmpty()) {
            player.sendMessage("Container is empty!");

            //JNBT.addIntTagToItem(droppedItemStack, "dropCount", 1);
            JNBT.addData(droppedItemStack, PersistentDataType.INTEGER, "dropCount", 1);
        }
        else {
            player.sendMessage("Container: " + JNBT.getData(droppedItemStack, PersistentDataType.INTEGER, "dropCount"));

            int count = JNBT.getData(droppedItemStack, PersistentDataType.INTEGER, "dropCount");

            JNBT.addData(droppedItemStack, PersistentDataType.INTEGER, "dropCount",count + 1);
        }

        if(player.isSneaking()) {
            JNBT.clearAllData(droppedItemStack);
            event.setCancelled(true);
        }
    }

    public static JoltingLib getInstance() {
        return instance;
    }
}
