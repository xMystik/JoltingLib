package me.skript.joltinglib.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JNBT {

    /**
     * Checks if a specific key exists on the item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item   the item to check
     * @param key    the key to look for
     * @return true if tag exists, false otherwise
     */
    public static boolean hasData(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey keyCheck = new NamespacedKey(plugin, key);

        return container.has(keyCheck, PersistentDataType.INTEGER)
                || container.has(keyCheck, PersistentDataType.STRING)
                || container.has(keyCheck, PersistentDataType.BOOLEAN)
                || container.has(keyCheck, PersistentDataType.BYTE)
                || container.has(keyCheck, PersistentDataType.DOUBLE)
                || container.has(keyCheck, PersistentDataType.FLOAT)
                || container.has(keyCheck, PersistentDataType.LONG);
    }

    /**
     * Adds certain data to the specified item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item   the item that will receive the data
     * @param type   {@link PersistentDataType} available data types
     * @param key    the key to store data into
     * @param value  the data to store
     */
    public static <K, V> void addData(Plugin plugin, ItemStack item, PersistentDataType<K, V> type, String key, V value) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), type, value);
        item.setItemMeta(meta);
    }

    /**
     * Receives the available data if they exist from the specified arguments
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item   the item that we will get the data from
     * @param type   {@link PersistentDataType} available data types
     * @param key    the key to receive the data from
     * @return the stored data depending on the type data
     */
    public static <K, V> V getData(Plugin plugin, ItemStack item, PersistentDataType<K, V> type, String key) {
        return getDataContainer(item).get(new NamespacedKey(plugin, key), type);
    }

    /**
     * Receive the Persistent Data Container from the specified item
     *
     * @param item that will get the data from
     * @return {@link PersistentDataContainer}
     * @throws IllegalArgumentException if Item or ItemMeta is null
     */
    public static PersistentDataContainer getDataContainer(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            throw new IllegalArgumentException("Item or ItemMeta cannot be null");
        }
        return item.getItemMeta().getPersistentDataContainer();
    }

    /**
     * Retrieves all tags present on the item
     *
     * @param item the item to get the tags from
     * @return a list of all tag keys, or an empty list if no tags are found
     */
    public static List<String> getAllTags(ItemStack item) {
        List<String> tags = new ArrayList<>();

        if (item == null || item.getItemMeta() == null) {
            return tags;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Set<NamespacedKey> keys = container.getKeys();

        for (NamespacedKey key : keys) {
            tags.add(key.getKey());
        }

        return tags;
    }

    /**
     * Clears all the available data from the specified item
     *
     * @param item the item to clear the data
     */
    public static void clearAllData(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Set<NamespacedKey> keys = container.getKeys();

        for (NamespacedKey key : keys) {
            container.remove(key);
        }

        item.setItemMeta(meta);
    }
}
