package me.skript.joltinglib.toasts;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JConfiguredToastTest {
    private YamlConfiguration config() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("enabled", true);
        config.set("material", "CHARCOAL");
        config.set("custom-model-data", 117);
        config.set("message", "<yellow>Level {level}!");
        return config;
    }
    @Test void missingAndDisabledAreSilent() {
        assertNull(JConfiguredToast.read(null));
        var config = config(); config.set("enabled", false); config.set("type", "bad");
        assertNull(JConfiguredToast.read(config));
    }
    @Test void acceptsFrameAliasesAndIcon() {
        var config = config();
        for (String type : new String[]{"t", "TASK", "c", "CHALLENGE", "g", "GOAL"}) {
            config.set("type", type);
            var result = JConfiguredToast.read(config);
            assertEquals(117, result.model());
            assertEquals(type.equalsIgnoreCase("t") || type.equals("TASK") ? JToastType.TASK
                    : type.equalsIgnoreCase("c") || type.equals("CHALLENGE") ? JToastType.CHALLENGE : JToastType.GOAL, result.type());
        }
    }
    @Test void rejectsInvalidModels() {
        for (Object model : new Object[]{-1, 1.5, "117", Double.NaN, 2147483648L}) {
            var config = config(); config.set("custom-model-data", model);
            assertThrows(IllegalArgumentException.class, () -> JConfiguredToast.read(config));
        }
    }
    @Test void rejectsInvalidFramesItemsAndText() {
        var config = config(); config.set("type", "nope");
        assertThrows(IllegalArgumentException.class, () -> JConfiguredToast.read(config));
        config.set("type", "g"); config.set("material", "AIR");
        assertThrows(IllegalArgumentException.class, () -> JConfiguredToast.read(config));
        config.set("material", "CHARCOAL"); config.set("message", " ");
        assertThrows(IllegalArgumentException.class, () -> JConfiguredToast.read(config));
    }
}
