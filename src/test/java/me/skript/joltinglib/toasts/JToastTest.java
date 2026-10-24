package me.skript.joltinglib.toasts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JToastTest {
    @Test void payloadRequiresOnlyTheNotificationCriterion() {
        JsonObject payload = JsonParser.parseString(JToast.payload(JToastType.TASK, icon(), "Test")).getAsJsonObject();
        assertEquals("notification", payload.getAsJsonArray("requirements").get(0).getAsJsonArray().get(0).getAsString());
        assertEquals(1, payload.getAsJsonObject("criteria").size());
    }

    private JsonObject icon() {
        return JsonParser.parseString("{\"id\":\"minecraft:charcoal\",\"count\":1,\"components\":{\"minecraft:custom_model_data\":{\"floats\":[117.0]}}}").getAsJsonObject();
    }

    @Test void preservesCustomModelComponents() {
        JsonObject item = icon();
        JsonObject display = JsonParser.parseString(JToast.payload(JToastType.TASK, item, "Test")).getAsJsonObject().getAsJsonObject("display");
        assertEquals(item, display.getAsJsonObject("icon"));
        assertEquals(117f, display.getAsJsonObject("icon").getAsJsonObject("components").getAsJsonObject("minecraft:custom_model_data").getAsJsonArray("floats").get(0).getAsFloat());
    }

    @Test void escapesTextAndSupportsLineBreaksAndColors() {
        JsonObject display = JsonParser.parseString(JToast.payload(JToastType.GOAL, icon(), "<yellow>A \"quote\"|C:\\icons")).getAsJsonObject().getAsJsonObject("display");
        String plain = PlainTextComponentSerializer.plainText().serialize(GsonComponentSerializer.gson().deserialize(display.get("title").toString()));
        assertEquals("A \"quote\"\nC:\\icons", plain);
        assertTrue(display.get("title").toString().contains("yellow"));
    }

    @Test void mapsAllFramesAndDisablesChatAnnouncement() {
        for (JToastType frame : JToastType.values()) {
            JsonObject payload = JsonParser.parseString(JToast.payload(frame, icon(), "Hello")).getAsJsonObject();
            JsonObject display = payload.getAsJsonObject("display");
            assertEquals(frame.name().toLowerCase(java.util.Locale.ROOT), display.get("frame").getAsString());
            assertTrue(display.get("show_toast").getAsBoolean());
            assertFalse(display.get("announce_to_chat").getAsBoolean());
            assertTrue(payload.getAsJsonObject("criteria").has("notification"));
        }
    }

    @Test void modelSetterIsFluentAndRejectsNegativeValues() {
        JToastBuilder builder = new JToastBuilder();
        assertSame(builder, builder.setCustomModelData(117));
        assertSame(builder, builder.setCustomModelData(0));
        assertThrows(IllegalArgumentException.class, () -> builder.setCustomModelData(-1));
    }

    @Test void keepsExistingSignaturesAndAddsItemAndModelOverloads() throws Exception {
        assertNotNull(JToastBuilder.class.getMethod("setIcon", Material.class));
        assertNotNull(JToastBuilder.class.getMethod("setIcon", ItemStack.class));
        assertNotNull(JToastBuilder.class.getMethod("send"));
        assertNotNull(JToast.class.getMethod("sendToast", Player.class, JToastType.class, Material.class, String.class));
        assertNotNull(JToast.class.getMethod("sendToast", Player.class, JToastType.class, Material.class, int.class, String.class));
        assertNotNull(JToast.class.getMethod("sendToast", Player.class, JToastType.class, ItemStack.class, String.class));
    }
}
