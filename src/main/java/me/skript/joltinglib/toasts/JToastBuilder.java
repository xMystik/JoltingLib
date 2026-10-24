package me.skript.joltinglib.toasts;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Objects;

public class JToastBuilder {

    private Player player;
    private JToastType type;
    private Material icon;
    private ItemStack itemIcon;
    private Integer customModelData;
    private String message;

    /**
     * @param player the player that receives the Toast
     */
    public JToastBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * @param type {@link JToastType} all available types
     */
    public JToastBuilder setType(JToastType type) {
        this.type = type;
        return this;
    }

    /**
     * @param icon the material to represent the Toast
     */
    public JToastBuilder setIcon(Material icon) {
        this.icon = Objects.requireNonNull(icon, "icon");
        this.itemIcon = null;
        return this;
    }

    /** Uses a defensive copy, preserving model data and other item components. */
    public JToastBuilder setIcon(ItemStack icon) {
        this.itemIcon = Objects.requireNonNull(icon, "icon").clone();
        this.icon = icon.getType();
        return this;
    }

    /** Overrides the icon's model; 0 clears it. Omit to preserve an ItemStack's model. */
    public JToastBuilder setCustomModelData(int customModelData) {
        if (customModelData < 0) throw new IllegalArgumentException("Custom model data cannot be negative");
        this.customModelData = customModelData;
        return this;
    }

    /**
     * @param message the message after the introduction of the Toast
     */
    public JToastBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sends the Toast message based on the previous arguments
     */
    public void send() {
        ItemStack snapshot = itemIcon == null ? new ItemStack(Objects.requireNonNull(icon, "icon")) : itemIcon.clone();
        JToast.applyModelData(snapshot, customModelData);
        JToast.sendToast(player, type, snapshot, message);
    }
}
