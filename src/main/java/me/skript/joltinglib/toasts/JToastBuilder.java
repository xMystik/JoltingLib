package me.skript.joltinglib.toasts;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class JToastBuilder {

    private Player player;
    private JToastType type;
    private Material icon;
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
        this.icon = icon;
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
        JToast.sendToast(player, type, icon, message);
    }
}
