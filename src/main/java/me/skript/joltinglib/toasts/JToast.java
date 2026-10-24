package me.skript.joltinglib.toasts;

import me.skript.joltinglib.JoltingLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.skript.joltinglib.text.JText;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

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
        validateMessage(message);
        ItemStack snapshot = snapshot(icon);
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

    static ItemStack snapshot(ItemStack icon) {
        Objects.requireNonNull(icon, "icon");
        if (icon.getType().isAir() || !icon.getType().isItem()) throw new IllegalArgumentException("Toast icon must be an item");
        ItemStack snapshot = icon.clone();
        snapshot.setAmount(1);
        return snapshot;
    }

    static void validateMessage(String message) {
        Objects.requireNonNull(message, "message");
        if (message.isBlank() || message.length() > 512) throw new IllegalArgumentException("Toast text must contain 1-512 characters");
    }

    /** Creates the toast representation; Core owns caching and player preferences. */
    public static String createPayload(JToastType type, ItemStack icon, String message) {
        Objects.requireNonNull(type, "type");
        validateMessage(message);
        return payload(type, Bukkit.getUnsafe().serializeItemAsJson(snapshot(icon)), message);
    }

    static String payload(JToastType type, JsonObject icon, String message) {
        JsonObject display = new JsonObject();
        display.add("icon", icon.deepCopy());
        display.add("title", JsonParser.parseString(GsonComponentSerializer.gson().serialize(
                JText.format(message.replace("|", "<newline>")))));
        display.addProperty("description", "");
        display.addProperty("frame", type.name().toLowerCase(Locale.ROOT));
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", false);
        display.addProperty("hidden", true);
        JsonObject trigger = new JsonObject();
        trigger.addProperty("trigger", "minecraft:impossible");
        JsonObject criteria = new JsonObject();
        criteria.add("notification", trigger);
        JsonObject payload = new JsonObject();
        payload.add("display", display);
        payload.add("criteria", criteria);
        JsonArray group = new JsonArray(); group.add("notification");
        JsonArray requirements = new JsonArray(); requirements.add(group);
        payload.add("requirements", requirements);
        return payload.toString();
    }

    private static void sendStandalone(Player player, JToastType type, ItemStack icon, String message) {
        NamespacedKey key = new NamespacedKey(JoltingLib.getInstance(), UUID.randomUUID().toString());
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, createPayload(type, icon, message));
        if (advancement == null) return;
        Bukkit.getUnsafe().removeAdvancement(key);
        player.getAdvancementProgress(advancement).awardCriteria("notification");
        Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), () -> {
            if (player.isOnline()) player.getAdvancementProgress(advancement).revokeCriteria("notification");
        }, 40L);
    }
}

