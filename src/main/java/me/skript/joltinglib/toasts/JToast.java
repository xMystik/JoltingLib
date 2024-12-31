package me.skript.joltinglib.toasts;

import me.skript.joltinglib.JDebug;
import me.skript.joltinglib.JoltingLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JToast {

    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final JToastType type;

    /**
     * Sends a Toast message towards a player
     *
     * @param player receiver of the message
     * @param type {@link JToastType} one of the available types
     * @param icon the Material that'll represent the message
     * @param message the message after the introduction of the Toast
     */
    protected static void sendToast(Player player, JToastType type, Material icon, String message) {
        new JToast(type, icon.name(), message).setup(player);
    }

    private JToast(JToastType type, String icon, String message) {
        this.key = new NamespacedKey(JoltingLib.getInstance(), UUID.randomUUID().toString());
        this.type = type;
        this.icon = icon;
        this.message = message;
    }

    private void setup(Player player) {
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), () -> revokeAdvancement(player), 5 );
    }

    private void createAdvancement() {
        if(JDebug.isVersionAtLeast("1.21")) {
            // Implements new format in case of 1.21+ server version.

            Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
                    "    \"display\": {\n" +
                    "        \"icon\": {\n" +
                    "            \"id\": \"minecraft:" + icon.toLowerCase() + "\",\n" +
                    "            \"count\": 1\n" +
                    "        },\n" +
                    "        \"title\": {\n" +
                    "            \"text\": \"" + message.replace("|", "\\n") + "\"\n" +
                    "        },\n" +
                    "        \"description\": {\n" +
                    "            \"text\": \"\"\n" +
                    "        },\n" +
                    "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                    "        \"frame\": \"" + type.toString().toLowerCase() + "\",\n" +
                    "        \"show_toast\": true,\n" +
                    "        \"announce_to_chat\": false,\n" +
                    "        \"hidden\": true\n" +
                    "    },\n" +
                    "    \"criteria\": {\n" +
                    "        \"trigger\": {\n" +
                    "            \"trigger\": \"minecraft:impossible\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}");
        } else {
            // Implements older format in case of 1.20 and below server version.

            Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
                    "    \"criteria\": {\n" +
                    "        \"trigger\": {\n" +
                    "            \"trigger\": \"minecraft:impossible\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"display\": {\n" +
                    "        \"icon\": {\n" +
                    "            \"item\": \"minecraft:" + icon.toLowerCase() + "\"\n" +
                    "        },\n" +
                    "        \"title\": {\n" +
                    "            \"text\": \"" + message.replace("|", "\n") + "\"\n" +
                    "        },\n" +
                    "        \"description\": {\n" +
                    "            \"text\": \"\"\n" +
                    "        },\n" +
                    "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                    "        \"frame\": \"" + type.toString().toLowerCase() + "\",\n" +
                    "        \"announce_to_chat\": false,\n" +
                    "        \"show_toast\": true,\n" +
                    "        \"hidden\": true\n" +
                    "    },\n" +
                    "    \"requirements\": [\n" +
                    "        [\n" +
                    "            \"trigger\"\n" +
                    "        ]\n" +
                    "    ]\n" +
                    "}");
        }
    }

    private void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
    }

}
