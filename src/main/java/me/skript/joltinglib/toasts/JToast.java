package me.skript.joltinglib.toasts;

import me.skript.joltinglib.JoltingLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class JToast {
    /** Existing material-only calls remain compatible. */
    public static void sendToast(Player player, JToastType type, Material icon, String message) {
        sendToast(player, type, new ItemStack(Objects.requireNonNull(icon, "icon")), message);
    }

    /** Custom model data of 0 means no custom model. */
    public static void sendToast(Player player, JToastType type, Material icon, int customModelData, String message) {
        if (customModelData < 0) throw new IllegalArgumentException("Custom model data cannot be negative");
        ItemStack stack = new ItemStack(Objects.requireNonNull(icon, "icon"));
        applyModelData(stack, customModelData);
        sendToast(player, type, stack, message);
    }

    /** Sends a snapshot of a complete icon, including Nexo/custom item components. */
    public static void sendToast(Player player, JToastType type, ItemStack icon, String message) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(message, "message");
        if (icon.getType().isAir() || !icon.getType().isItem()) throw new IllegalArgumentException("Toast icon must be an item");
        if (message.isBlank() || message.length() > 512) throw new IllegalArgumentException("Toast text must contain 1-512 characters");
        ItemStack snapshot = icon.clone();
        snapshot.setAmount(1);
        Runnable delivery = () -> {
            if (!player.isOnline()) return;
            try {
                JToastService service = Bukkit.getServicesManager().load(JToastService.class);
                if (service != null) {
                    service.sendToast(player, type, snapshot, message);
                    return; // A muted notification must never fall back to another transport.
                }
                sendStandalone(player, type, snapshot, message);
            } catch (Exception exception) {
                JoltingLib.getInstance().getLogger().warning("Could not deliver toast: " + exception.getMessage());
            }
        };
        if (Bukkit.isPrimaryThread()) delivery.run();
        else Bukkit.getScheduler().runTask(JoltingLib.getInstance(), delivery);
    }

    static void applyModelData(ItemStack icon, Integer model) {
        if (model == null) return;
        if (model < 0) throw new IllegalArgumentException("Custom model data cannot be negative");
        icon.editMeta(meta -> meta.setCustomModelData(model == 0 ? null : model));
    }

    private static void sendStandalone(Player player, JToastType type, ItemStack icon, String message) {
        NamespacedKey key = new NamespacedKey(JoltingLib.getInstance(), UUID.randomUUID().toString());
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key,
                JToastPayload.json(type, Bukkit.getUnsafe().serializeItemAsJson(icon), message));
        if (advancement == null) return;
        // Paper retains the runtime advancement; remove its generated disk file immediately.
        Bukkit.getUnsafe().removeAdvancement(key);
        player.getAdvancementProgress(advancement).awardCriteria("notification");
        Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), () -> {
            if (player.isOnline()) player.getAdvancementProgress(advancement).revokeCriteria("notification");
        }, 40L);
    }
}
