package me.skript.joltinglib.toasts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Optional shared transport registered through Bukkit's ServicesManager.
 * The service owns delivery and player preferences; suppression must not fall back to another transport.
 */
public interface JToastService {
    void sendToast(Player player, JToastType type, ItemStack icon, String message);
}
