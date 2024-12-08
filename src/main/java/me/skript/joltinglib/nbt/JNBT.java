package me.skript.joltinglib.nbt;

import me.skript.joltinglib.JoltingLib;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class JNBT {

    public static void addStringTagToItem(ItemStack item, NamespacedKey key, String value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void addStringTagToItem(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(JoltingLib.getInstance(), key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void addIntTagToItem(ItemStack item, NamespacedKey key, int value) {
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
            item.setItemMeta(meta);
        }
    }

    public static void addIntTagToItem(ItemStack item, String key, int value) {
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(JoltingLib.getInstance(), key), PersistentDataType.INTEGER, value);
            item.setItemMeta(meta);
        }
    }

    public static String getStringTagFromItem(ItemStack item, NamespacedKey key) {
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static String getStringTagFromItem(ItemStack item, String key) {
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(JoltingLib.getInstance(), key), PersistentDataType.STRING);
    }

    public static int getIntTagFromItem(ItemStack item, NamespacedKey key) {
        if (item != null && item.getItemMeta() != null) {
            Integer value = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            return (value != null) ? value : 0; // Return 0 if no value found
        }
        return 0; // Return 0 if the item or item meta is null
    }

    public static int getIntTagFromItem(ItemStack item, String key) {
        if (item != null && item.getItemMeta() != null) {
            Integer value = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(JoltingLib.getInstance(), key), PersistentDataType.INTEGER);
            return (value != null) ? value : 0; // Return 0 if no value found
        }
        return 0; // Return 0 if the item or item meta is null
    }
}
