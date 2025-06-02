package me.skript.joltinglib.items;

import me.skript.joltinglib.text.JText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;
    private final boolean supportsComponents;

    /**
     * Creates a new {@code JItemBuilder} with the specified material
     *
     * @param material the {@link Material} of the item to build
     */
    public JItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
        this.supportsComponents = isComponentSupported();
    }

    /**
     * Checks if the server version supports Adventure {@link Component} for display names and lore
     *
     * @return true if components are supported, false otherwise
     */
    private boolean isComponentSupported() {
        try {
            ItemMeta.class.getMethod("displayName", Component.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Sets the display name of the item using MiniMessage for color codes
     *
     * @param displayName the new display name String
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setDisplayName(String displayName) {
        if (supportsComponents) {
            meta.displayName(JText.format(displayName));
        } else {
            meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(JText.format(displayName)));
        }
        return this;
    }

    /**
     * Sets the display name of the item using MiniMessage for color codes
     *
     * @param displayName the new display name Component
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setDisplayName(Component displayName) {
        if (supportsComponents) {
            meta.displayName(displayName);
        } else {
            meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayName));
        }
        return this;
    }

    /**
     * Sets the lore of the item using MiniMessage for color codes
     *
     * @param lore a list of lore lines
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setLoreFromStringList(List<String> lore) {
        if (supportsComponents) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(JText.format(line));
            }
            meta.lore(loreComponents);
        } else {
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(LegacyComponentSerializer.legacySection().serialize(JText.format(line)));
            }
            meta.setLore(formattedLore);
        }
        return this;
    }

    /**
     * Sets the lore of the item using a list of Adventure Components
     *
     * @param lore a list of Component objects
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setLoreFromComponentList(List<Component> lore) {
        if (supportsComponents) {
            meta.lore(new ArrayList<>(lore));
        } else {
            List<String> formattedLore = new ArrayList<>();
            for (Component line : lore) {
                formattedLore.add(LegacyComponentSerializer.legacySection().serialize(line));
            }
            meta.setLore(formattedLore);
        }
        return this;
    }

    /**
     * Sets a single line of lore for the item using MiniMessage for color codes
     *
     * @param lore the lore line
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setLore(String lore) {
        return setLoreFromStringList(List.of(lore));
    }

    /**
     * Sets a single line of lore for the item using an Adventure Component
     *
     * @param lore the lore line as a Component
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setLore(Component lore) {
        return setLoreFromComponentList(List.of(lore));
    }

    /**
     * Adds a line to the item's existing lore using MiniMessage for color codes
     *
     * @param line the new lore line
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder addLore(String line) {
        if (supportsComponents) {
            List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
            lore.add(JText.format(line));
            meta.lore(lore);
        } else {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
            lore.add(LegacyComponentSerializer.legacySection().serialize(JText.format(line)));
            meta.setLore(lore);
        }
        return this;
    }


    /**
     * Adds a line to the item's existing lore using an Adventure Component
     *
     * @param line the new lore line as a Component
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder addLore(Component line) {
        if (supportsComponents) {
            List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
            lore.add(line);
            meta.lore(lore);
        } else {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
            lore.add(LegacyComponentSerializer.legacySection().serialize(line));
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Sets the owning player for a player skull
     *
     * @param playerUUID the UUID of the player to associate with the skull
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setPlayerSkull(UUID playerUUID) {
        if (meta instanceof SkullMeta skullMeta) {

            // Set the owning player of the skull
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));

            item.setItemMeta(skullMeta);
        }
        return this;
    }

    /**
     * Sets the custom model data for the item
     *
     * @param customModelData the custom model data value
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    /**
     * Sets the amount of items in the stack
     *
     * @param amount the amount, limited between 1 and 64
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setAmount(int amount) {
        item.setAmount(Math.max(1, Math.min(amount, 64)));
        return this;
    }

    /**
     * Sets the item's durability
     *
     * @param durability the durability value
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setDurability(int durability) {
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(Math.max(0, durability));
            item.setItemMeta(damageable);
        }
        return this;
    }

    /**
     * Adds an enchantment to the item
     *
     * @param enchantment the {@link Enchantment} to add
     * @param level the level of the enchantment
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adds item flags to the item
     *
     * @param flags the {@link ItemFlag}s to add
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder addItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Sets whether the item is unbreakable
     *
     * @param unbreakable true to make the item unbreakable, false otherwise
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Adds custom data to the item using the Persistent Data API
     *
     * @param plugin the plugin instance to use for the NamespacedKey
     * @param type the {@link PersistentDataType} of the data
     * @param key the key for the data
     * @param value the value to store
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public <K, V> JItemBuilder addData(Plugin plugin, PersistentDataType<K, V> type, String key, V value) {
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), type, value);
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Adds a glint effect to the item being built
     *
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder addGlint() {
        if (item == null || item.getType() == Material.AIR) {
            return this;
        }

        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Builds the final {@link ItemStack} based on the current builder state
     *
     * @return the constructed {@link ItemStack}
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Adds a glint to the given ItemStack
     *
     * @param item The ItemStack to add the glint
     */
    public static void addGlint(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    /**
     * Removes the glint from the given ItemStack
     *
     * @param item The ItemStack to remove the glint
     */
    public static void removeGlint(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.getEnchants().keySet().forEach(meta::removeEnchant);
        item.setItemMeta(meta);
    }

    /**
     * Checks if the given ItemStack has a glint effect
     *
     * @param item The ItemStack to check
     * @return True if the ItemStack has a glint, false otherwise
     */
    public static boolean hasGlint(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.hasEnchant(Enchantment.PROTECTION) && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }
}