package me.skript.joltinglib.items;

import me.skript.joltinglib.JoltingLib;
import me.skript.joltinglib.colorcodes.JText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

import java.util.ArrayList;
import java.util.List;

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
     * @param displayName the new display name
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setDisplayName(String displayName) {
        if (supportsComponents) {
            meta.displayName(MiniMessage.miniMessage().deserialize(displayName));
        } else {
            meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(MiniMessage.miniMessage().deserialize(displayName)
            ));
        }
        return this;
    }

    /**
     * Sets the lore of the item using MiniMessage for color codes
     *
     * @param lore a list of lore lines
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setLore(List<String> lore) {
        if (supportsComponents) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(JText.format(line));
            }
            meta.lore(loreComponents);
        } else {
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(LegacyComponentSerializer.legacySection().serialize(JText.format(line)
                ));
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
        return setLore(List.of(lore));
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
     * Sets the owning player for a player skull
     *
     * @param playerName the name of the player to associate with the skull
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public JItemBuilder setPlayerSkull(String playerName) {
        if (meta instanceof SkullMeta skullMeta) {

            // Set the owning player of the skull
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

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
     * @param type the {@link PersistentDataType} of the data
     * @param key the key for the data
     * @param value the value to store
     * @return the current {@code JItemBuilder} instance for chaining
     */
    public <K, V> JItemBuilder addData(PersistentDataType<K, V> type, String key, V value) {
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(JoltingLib.getInstance(), key), type, value);
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
}