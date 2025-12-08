package me.skript.joltinglib.holograms;

import me.skript.joltinglib.JoltingLib;
import me.skript.joltinglib.text.JText;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builder for creating fully customizable holograms using TextDisplay entity
 */
public class JHologramBuilder {

    private TextDisplay display;
    private final Location location;
    private final List<Component> lines = new ArrayList<>();

    private int duration = 40;
    private float scale = 1.0f;

    private boolean shadowed = true;
    private boolean seeThrough = false;

    private Color backgroundColor = null;
    private boolean backgroundTransparent = true;

    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private Display.Billboard pivot = Display.Billboard.CENTER;
    private Display.Brightness brightness = new Display.Brightness(15, 15);

    private double floatHeight = 0;
    private int floatDuration = 30;

    private boolean fadeOut = false;

    private List<Player> viewers = new ArrayList<>();

    /**
     * Creates a new hologram builder at the specified location
     *
     * @param location The world location where the hologram will spawn
     */
    public JHologramBuilder(Location location) {
        this.location = location.clone();
    }

    /**
     * Adds a single line of formatted text to the hologram
     *
     * @param text Text to add (supports MiniMessage through {@link JText#format})
     */
    public JHologramBuilder addLine(String text) {
        lines.add(JText.format(text));
        return this;
    }

    /**
     * Adds multiple lines of formatted text to the hologram
     *
     * @param textLines A list of raw text lines to append
     */
    public JHologramBuilder addLines(List<String> textLines) {
        textLines.forEach(s -> lines.add(JText.format(s)));
        return this;
    }

    /**
     * Sets how long the hologram should remain before being removed
     *
     * @param ticks Duration in ticks (minimum 1 tick)
     */
    public JHologramBuilder setDuration(int ticks) {
        this.duration = Math.max(1, ticks);
        return this;
    }

    /**
     * Sets the scale multiplier of the hologram text
     *
     * @param scale A scale factor (minimum 0.01)
     */
    public JHologramBuilder setScale(float scale) {
        this.scale = Math.max(0.01f, scale);
        return this;
    }

    /**
     * Sets whether the hologram can be seen through walls
     *
     * @param toggle True to enable see-through mode
     */
    public JHologramBuilder setSeeThrough(boolean toggle) {
        this.seeThrough = toggle;
        return this;
    }

    /**
     * Sets the alignment of the hologram's text
     *
     * @param alignment LEFT / CENTER / RIGHT
     */
    public JHologramBuilder setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Sets the billboard mode determining how the hologram faces players
     *
     * @param pivot A {@link Display.Billboard} mode
     */
    public JHologramBuilder setPivot(Display.Billboard pivot) {
        this.pivot = pivot;
        return this;
    }

    /**
     * Sets the emitted light level for both block and sky lighting
     *
     * @param brightness Brightness level (0–15)
     */
    public JHologramBuilder setBrightness(int brightness) {
        brightness = Math.max(0, Math.min(brightness, 15));
        this.brightness = new Display.Brightness(brightness, brightness);
        return this;
    }

    /**
     * Enables or disables shadow rendering under the text
     *
     * @param shadowed True to render a shadow
     */
    public JHologramBuilder setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        return this;
    }

    /**
     * Sets a solid background color behind the hologram text
     *
     * @param color A {@link Color} object
     */
    public JHologramBuilder setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Enables or disables text background transparency
     *
     * @param transparent True to hide the background
     */
    public JHologramBuilder setBackgroundTransparent(boolean transparent) {
        this.backgroundTransparent = transparent;
        return this;
    }

    /**
     * Restricts visibility of the hologram to a specific list of players
     *
     * @param players Players who should see the hologram
     */
    public JHologramBuilder setViewers(List<Player> players) {
        this.viewers = players;
        return this;
    }

    /**
     * Restricts hologram visibility to a single player
     *
     * @param player Player who should see the hologram
     */
    public JHologramBuilder setViewer(Player player) {
        this.viewers = List.of(player);
        return this;
    }

    /**
     * Sets how high the hologram should float upward from its initial position
     *
     * @param height Vertical offset in blocks
     */
    public JHologramBuilder setFloatHeight(double height) {
        this.floatHeight = Math.max(0, height);
        return this;
    }

    /**
     * Sets the duration of the floating animation
     *
     * @param ticks Duration in ticks (0–59)
     */
    public JHologramBuilder setFloatDuration(int ticks) {
        this.floatDuration = Math.max(0, Math.min(ticks, 59));
        return this;
    }

    /**
     * Enables or disables automatic fade-out.
     *
     * @param toggle true to fade-out, false for no fade animation
     */
    public JHologramBuilder setFadeOut(boolean toggle) {
        this.fadeOut = toggle;
        return this;
    }

    /**
     * Builds and spawns the hologram in the world with all configured settings applied
     *
     * @return The spawned {@link TextDisplay}, or null if location/world is invalid
     */
    public TextDisplay build() {
        World world = location.getWorld();
        if (world == null) return null;

        display = world.spawn(location, TextDisplay.class, display -> {
            display.setTeleportDuration(floatDuration);
        });

        // Text
        if (lines.isEmpty()) {
            display.text(Component.text(""));
        } else {
            Component full = Component.empty();
            for (int i = 0; i < lines.size(); i++) {
                full = full.append(lines.get(i));
                if (i < lines.size() - 1) full = full.append(Component.newline());
            }
            display.text(full);
        }

        // Settings
        display.setBillboard(pivot);
        display.setSeeThrough(seeThrough);
        display.setAlignment(alignment);
        display.setBrightness(brightness);
        display.setShadowed(shadowed);

        if (backgroundTransparent) {
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0));
        } else {
            display.setBackgroundColor(backgroundColor);
        }

        // Scale
        Transformation t = display.getTransformation();
        t.getScale().set(scale);
        t.getTranslation().set(0, 0, 0);
        display.setTransformation(t);

        // Float
        if (floatHeight > 0) {
            Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), task -> {
                if (!display.isValid()) {
                    task.cancel();
                    return;
                }

                Location targetLocation = display.getLocation().clone().add(0, floatHeight, 0);
                display.teleport(targetLocation);
            }, 2L);
        }

        // Fade & Duration
        int fadeLength = fadeOut ? Math.min(40, duration) : 0;
        int fadeStart = duration - fadeLength;

        AtomicInteger tick = new AtomicInteger(0);

        Bukkit.getScheduler().runTaskTimer(JoltingLib.getInstance(), task -> {
            int now = tick.getAndIncrement();

            // Remove
            if (now >= duration) {
                if (display != null && !display.isDead()) display.remove();
                task.cancel();
                return;
            }

            // Fade-out
            if (fadeOut && now >= fadeStart) {
                double progress = (double)(now - fadeStart) / fadeLength;
                int opacity = (int)(255 - (progress * 255));
                display.setTextOpacity((byte) opacity);
            }

        }, 1L, 1L);

        // Viewers
        if (!viewers.isEmpty()) {
            display.setVisibleByDefault(false);

            for (Player viewer : viewers) {
                if (viewer.isOnline()) {
                    viewer.showEntity(JoltingLib.getInstance(), display);
                }
            }
        }

        return display;
    }
}
