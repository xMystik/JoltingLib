package me.skript.joltinglib.nbt;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JNBT {

    private JNBT() {}

    /**
     * Checks if a specific key exists on the item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item the item to check
     * @param key the key to look for
     * @return true if tag exists, false otherwise
     */
    public static boolean hasData(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

        return container.has(namespacedKey, PersistentDataType.INTEGER)
                || container.has(namespacedKey, PersistentDataType.STRING)
                || container.has(namespacedKey, PersistentDataType.BOOLEAN)
                || container.has(namespacedKey, PersistentDataType.BYTE)
                || container.has(namespacedKey, PersistentDataType.DOUBLE)
                || container.has(namespacedKey, PersistentDataType.FLOAT)
                || container.has(namespacedKey, PersistentDataType.LONG);
    }

    /**
     * Checks if a specific key exists on the player
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param player the player to check
     * @param key the key to look for
     * @return true if tag exists, false otherwise
     */
    public static boolean hasData(Plugin plugin, Player player, String key) {
        if (player == null) {
            return false;
        }
        return hasData(plugin, (Entity) player, key);
    }

    /**
     * Checks if a specific key exists on the entity
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param entity the entity to check
     * @param key the key to look for
     * @return true if tag exists, false otherwise
     */
    public static boolean hasData(Plugin plugin, Entity entity, String key) {
        if (entity == null) {
            return false;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return container.has(namespacedKey, PersistentDataType.INTEGER)
                || container.has(namespacedKey, PersistentDataType.STRING)
                || container.has(namespacedKey, PersistentDataType.BOOLEAN)
                || container.has(namespacedKey, PersistentDataType.BYTE)
                || container.has(namespacedKey, PersistentDataType.DOUBLE)
                || container.has(namespacedKey, PersistentDataType.FLOAT)
                || container.has(namespacedKey, PersistentDataType.LONG);
    }

    /**
     * Checks if a specific key exists on the block's state
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param state the block state to check
     * @param key the key to look for
     * @return true if tag exists, false otherwise
     */
    public static boolean hasData(Plugin plugin, BlockState state, String key) {
        if (state == null) {
            return false;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
        NamespacedKey namespacedKey = getBlockNamespacedKey(plugin, state, key);
        return container.has(namespacedKey, PersistentDataType.INTEGER)
                || container.has(namespacedKey, PersistentDataType.STRING)
                || container.has(namespacedKey, PersistentDataType.BOOLEAN)
                || container.has(namespacedKey, PersistentDataType.BYTE)
                || container.has(namespacedKey, PersistentDataType.DOUBLE)
                || container.has(namespacedKey, PersistentDataType.FLOAT)
                || container.has(namespacedKey, PersistentDataType.LONG);
    }

    /**
     * Adds certain data to the specified item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item the item that will receive the data
     * @param type {@link PersistentDataType} available data types
     * @param key the key to store data into
     * @param value the data to store
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
     * Adds certain data to the specified player
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param player the player that will receive the data
     * @param type {@link PersistentDataType} available data types
     * @param key the key to store data into
     * @param value the data to store
     */
    public static <K, V> void addData(Plugin plugin, Player player, PersistentDataType<K, V> type, String key, V value) {
        if (player == null) {
            return;
        }
        addData(plugin, (Entity) player, type, key, value);
    }

    /**
     * Adds certain data to the specified entity
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param entity the entity that will receive the data
     * @param type {@link PersistentDataType} available data types
     * @param key the key to store data into
     * @param value the data to store
     */
    public static <K, V> void addData(Plugin plugin, Entity entity, PersistentDataType<K, V> type, String key, V value) {
        if (entity == null) {
            return;
        }
        entity.getPersistentDataContainer().set(new NamespacedKey(plugin, key), type, value);
    }

    /**
     * Adds certain data to the specified block's state
     * <p>
     * If the BlockState supports its own PersistentDataContainer, its data is updated and then state.update() is called.
     * Otherwise, the block’s chunk container is used
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param state the block state that will receive the data
     * @param type {@link PersistentDataType} available data types
     * @param key the key to store data into
     * @param value the data to store
     */
    public static <K, V> void addData(Plugin plugin, BlockState state, PersistentDataType<K, V> type, String key, V value) {
        if (state == null) {
            return;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
        NamespacedKey namespacedKey = getBlockNamespacedKey(plugin, state, key);
        container.set(namespacedKey, type, value);
        // If the state itself is modifiable, update it.
        if (state instanceof PersistentDataHolder) {
            state.update();
        }
    }

    /**
     * Removes the data associated with the specified key from the item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item the item to remove the data from
     * @param key the key to remove
     */
    public static void removeData(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
        item.setItemMeta(meta);
    }

    /**
     * Removes the data associated with the specified key from the player
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param player the player to remove the data from
     * @param key the key to remove
     */
    public static void removeData(Plugin plugin, Player player, String key) {
        if (player == null) {
            return;
        }
        removeData(plugin, (Entity) player, key);
    }

    /**
     * Removes the data associated with the specified key from the entity
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param entity the entity to remove the data from
     * @param key the key to remove
     */
    public static void removeData(Plugin plugin, Entity entity, String key) {
        if (entity == null) {
            return;
        }
        entity.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
    }

    /**
     * Removes the data associated with the specified key from the block's state
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param state the block state to remove the data from
     * @param key the key to remove
     */
    public static void removeData(Plugin plugin, BlockState state, String key) {
        if (state == null) {
            return;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
        NamespacedKey namespacedKey = getBlockNamespacedKey(plugin, state, key);
        container.remove(namespacedKey);
        if (state instanceof PersistentDataHolder) {
            state.update();
        }
    }

    /**
     * Receives the available data if it exists from the specified item
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param item the item that we will get the data from
     * @param type {@link PersistentDataType} available data types
     * @param key the key to receive the data from
     * @return the stored data depending on the type data, or null if not found
     */
    public static <K, V> V getData(Plugin plugin, ItemStack item, PersistentDataType<K, V> type, String key) {
        return getDataContainer(item).get(new NamespacedKey(plugin, key), type);
    }

    /**
     * Receives the available data if it exists from the specified player
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param player the player to get the data from
     * @param type {@link PersistentDataType} available data types
     * @param key the key to receive the data from
     * @return the stored data depending on the type data, or null if not found
     */
    public static <K, V> V getData(Plugin plugin, Player player, PersistentDataType<K, V> type, String key) {
        if (player == null) {
            return null;
        }
        return getData(plugin, (Entity) player, type, key);
    }

    /**
     * Receives the available data if it exists from the specified entity
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param entity the entity to get the data from
     * @param type {@link PersistentDataType} available data types
     * @param key the key to receive the data from
     * @return the stored data depending on the type data, or null if not found
     */
    public static <K, V> V getData(Plugin plugin, Entity entity, PersistentDataType<K, V> type, String key) {
        if (entity == null) {
            return null;
        }
        return entity.getPersistentDataContainer().get(new NamespacedKey(plugin, key), type);
    }

    /**
     * Receives the available data if it exists from the specified block's state
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param state the block state to get the data from
     * @param type {@link PersistentDataType} available data types
     * @param key the key to receive the data from
     * @return the stored data depending on the type data, or null if not found
     */
    public static <K, V> V getData(Plugin plugin, BlockState state, PersistentDataType<K, V> type, String key) {
        if (state == null) {
            return null;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
        NamespacedKey namespacedKey = getBlockNamespacedKey(plugin, state, key);
        return container.get(namespacedKey, type);
    }

    /**
     * Retrieves all tags present on the item
     *
     * @param item the item to get the tags from
     * @return a list of all tag keys, or an empty list if no tags are found
     */
    public static List<String> getAllData(ItemStack item) {
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
     * Retrieves all tags present on the player
     *
     * @param player the player to get the tags from
     * @return a list of all tag keys, or an empty list if no tags are found
     */
    public static List<String> getAllData(Player player) {
        return getAllData((Entity) player);
    }

    /**
     * Retrieves all tags present on the entity
     *
     * @param entity the entity to get the tags from
     * @return a list of all tag keys, or an empty list if no tags are found
     */
    public static List<String> getAllData(Entity entity) {
        List<String> tags = new ArrayList<>();
        if (entity == null) {
            return tags;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        Set<NamespacedKey> keys = container.getKeys();
        for (NamespacedKey key : keys) {
            tags.add(key.getKey());
        }
        return tags;
    }

    /**
     * Retrieves all tags present on the block's state
     *
     * @param state the block state to get the tags from
     * @return a list of all tag keys, or an empty list if no tags are found
     */
    public static List<String> getAllData(BlockState state) {
        List<String> tags = new ArrayList<>();
        if (state == null) {
            return tags;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
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

    /**
     * Clears all the available data from the specified player
     *
     * @param player the player to clear the data
     */
    public static void clearAllData(Player player) {
        clearAllData((Entity) player);
    }

    /**
     * Clears all the available data from the specified entity
     *
     * @param entity the entity to clear the data
     */
    public static void clearAllData(Entity entity) {
        if (entity == null) {
            return;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        Set<NamespacedKey> keys = container.getKeys();
        for (NamespacedKey key : keys) {
            container.remove(key);
        }
    }

    /**
     * Clears all the available data from the specified block's state
     *
     * @param state the block state to clear the data
     */
    public static void clearAllData(BlockState state) {
        if (state == null) {
            return;
        }
        PersistentDataContainer container = getBlockDataContainer(state);
        Set<NamespacedKey> keys = container.getKeys();
        for (NamespacedKey key : keys) {
            container.remove(key);
        }
        if (state instanceof PersistentDataHolder) {
            state.update();
        }
    }

    /**
     * Receive the Persistent Data Container from the specified item
     *
     * @param item that will get the data from
     * @return {@link PersistentDataContainer}
     * @throws IllegalArgumentException if Item or ItemMeta is null
     */
    private static PersistentDataContainer getDataContainer(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            throw new IllegalArgumentException("Item or ItemMeta cannot be null");
        }
        return item.getItemMeta().getPersistentDataContainer();
    }

    /**
     * Helper method to get a PersistentDataContainer for a BlockState
     * <p>
     * If the state implements PersistentDataHolder (i.e. is a TileState),
     * then its own container is used; otherwise, the block’s chunk container is used
     *
     * @param state the block state
     * @return the appropriate PersistentDataContainer
     */
    private static PersistentDataContainer getBlockDataContainer(BlockState state) {
        if (state instanceof PersistentDataHolder) {
            return ((PersistentDataHolder) state).getPersistentDataContainer();
        } else {
            Chunk chunk = state.getWorld().getChunkAt(state.getLocation());
            return chunk.getPersistentDataContainer();
        }
    }

    /**
     * Helper method to generate a NamespacedKey for a block
     * <p>
     * If the state is a PersistentDataHolder, the key is used as-is
     * Otherwise, a composite key including the block’s coordinates is created
     *
     * @param plugin the plugin instance to use
     * @param state  the block state
     * @param key    the base key
     * @return the NamespacedKey to use
     */
    private static NamespacedKey getBlockNamespacedKey(Plugin plugin, BlockState state, String key) {
        if (state instanceof PersistentDataHolder) {
            return new NamespacedKey(plugin, key);
        } else {
            Location loc = state.getLocation();
            String compositeKey = loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ() + "_" + key;
            return new NamespacedKey(plugin, compositeKey);
        }
    }
}
