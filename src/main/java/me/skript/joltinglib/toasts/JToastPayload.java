package me.skript.joltinglib.toasts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.skript.joltinglib.text.JText;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import java.util.Locale;

/** Server-serialized icons preserve the current component format. */
final class JToastPayload {
    private JToastPayload() {}

    static String json(JToastType type, JsonObject icon, String message) {
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
        return payload.toString();
    }
}
