package me.skript.joltinglib.toasts;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/** Config-backed toast delivery. Invalid cosmetics never interrupt the underlying action. */
public final class JConfiguredToast {
    private static final Map<String, String> WARNINGS = new ConcurrentHashMap<>();
    private JConfiguredToast() {}

    public record Definition(boolean enabled, JToastType type, Material material, int model, String message) {}

    public static Definition read(ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", false)) return null;
        String value = section.getString("type", "t").trim().toUpperCase(Locale.ROOT);
        JToastType type = switch (value) {
            case "T", "TASK" -> JToastType.TASK;
            case "C", "CHALLENGE" -> JToastType.CHALLENGE;
            case "G", "GOAL" -> JToastType.GOAL;
            default -> throw new IllegalArgumentException("type must be t, c or g");
        };
        Material material = Material.matchMaterial(section.getString("material", "CHARCOAL"));
        // Item availability is checked by JToast against the running server's registry.
        if (material == null || material == Material.AIR || material == Material.CAVE_AIR || material == Material.VOID_AIR)
            throw new IllegalArgumentException("material must be a valid item");
        Object rawModel = section.get("custom-model-data", 0);
        if (!(rawModel instanceof Number number) || number.doubleValue() < 0 || number.doubleValue() > Integer.MAX_VALUE
                || number.doubleValue() != number.intValue()) throw new IllegalArgumentException("custom-model-data must be a non-negative integer");
        String message = section.getString("message", "");
        if (message.isBlank() || message.length() > 512) throw new IllegalArgumentException("message must contain 1-512 characters");
        return new Definition(true, type, material, number.intValue(), message);
    }

    public static void send(Plugin plugin, ConfigurationSection messages, String path, Player player, Map<String, String> values) {
        send(plugin, messages, path, player, text -> {
            for (Map.Entry<String, String> entry : values.entrySet()) text = text.replace("{" + entry.getKey() + "}", entry.getValue());
            return text;
        });
    }

    public static void send(Plugin plugin, ConfigurationSection messages, String path, Player player, UnaryOperator<String> placeholders) {
        if (player == null) return;
        String key = plugin.getName() + ":" + path;
        try {
            Definition toast = read(messages == null ? null : messages.getConfigurationSection(path));
            if (toast == null) return;
            String text = placeholders.apply(toast.message()).replace("{player}", player.getName());
            new JToastBuilder().setPlayer(player).setType(toast.type()).setIcon(toast.material())
                    .setCustomModelData(toast.model()).setMessage(text).send();
            WARNINGS.remove(key);
        } catch (RuntimeException exception) {
            String reason = String.valueOf(exception.getMessage());
            if (!reason.equals(WARNINGS.put(key, reason))) plugin.getLogger().warning("Toast '" + path + "' skipped: " + reason);
        }
    }
}
