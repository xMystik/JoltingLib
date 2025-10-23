package me.skript.joltinglib.bossbars;

import me.skript.joltinglib.text.JText;
import org.bukkit.Bukkit;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JBossBar {

    private static final Map<String, BossBar> bossBars = new ConcurrentHashMap<>();
    private static final Map<String, BukkitRunnable> activeAnimations = new ConcurrentHashMap<>();

    private JBossBar() {}

    // -------------------- Creation --------------------

    /**
     * Creates a new BossBar if it does not already exist.
     *
     * @param id     Unique identifier for the boss bar.
     * @param title  Title of the boss bar (supports legacy formatting codes).
     * @param color  Color of the boss bar.
     * @param style  Style of the boss bar (e.g. SOLID, SEGMENTED_10, etc.).
     */
    public static void createBossBar(String id, String title, BarColor color, BarStyle style) {
        bossBars.computeIfAbsent(id, key -> Bukkit.createBossBar(JText.formatLegacy(title), color, style));
    }

    /**
     * Displays the specified boss bar to a single player.
     *
     * @param id      ID of the boss bar to show.
     * @param player  Player to display the boss bar to.
     */
    public static void showBossBar(String id, Player player) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.addPlayer(player);
    }

    /**
     * Hides the specified boss bar from a single player.
     *
     * @param id      ID of the boss bar to hide.
     * @param player  Player to remove from the boss bar.
     */
    public static void hideBossBar(String id, Player player) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.removePlayer(player);
    }

    /**
     * Displays the specified boss bar to a list of players.
     *
     * @param id       ID of the boss bar to show.
     * @param players  List of players to display the boss bar to.
     */
    public static void showBossBar(String id, List<Player> players) {
        BossBar bar = bossBars.get(id);
        if (bar != null && players != null) players.forEach(bar::addPlayer);
    }

    /**
     * Hides the specified boss bar from a list of players.
     *
     * @param id       ID of the boss bar to hide.
     * @param players  List of players to remove from the boss bar.
     */
    public static void hideBossBar(String id, List<Player> players) {
        BossBar bar = bossBars.get(id);
        if (bar != null && players != null) players.forEach(bar::removePlayer);
    }

    /**
     * Displays the specified boss bar to all online players.
     *
     * @param id  ID of the boss bar to show.
     */
    public static void showToAll(String id) {
        BossBar bar = bossBars.get(id);
        if (bar != null) Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
    }

    /**
     * Hides the specified boss bar from all online players.
     *
     * @param id  ID of the boss bar to hide.
     */
    public static void hideFromAll(String id) {
        BossBar bar = bossBars.get(id);
        if (bar != null) Bukkit.getOnlinePlayers().forEach(bar::removePlayer);
    }

    /**
     * Sets the title of an existing boss bar.
     *
     * @param id     ID of the boss bar to update.
     * @param title  New title text (supports legacy formatting codes).
     */
    public static void setTitle(String id, String title) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.setTitle(JText.formatLegacy(title));
    }

    /**
     * Sets the progress of a boss bar.
     * The value will be automatically clamped between 0 and 1.
     *
     * @param id        ID of the boss bar to modify.
     * @param progress  New progress value (0.0–1.0).
     */
    public static void setProgress(String id, double progress) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.setProgress(clamp(progress));
    }

    /**
     * Sets the color of an existing boss bar.
     *
     * @param id     ID of the boss bar to modify.
     * @param color  New BarColor to apply.
     */
    public static void setColor(String id, BarColor color) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.setColor(color);
    }

    /**
     * Sets the style of an existing boss bar.
     *
     * @param id     ID of the boss bar to modify.
     * @param style  New BarStyle to apply.
     */
    public static void setStyle(String id, BarStyle style) {
        BossBar bar = bossBars.get(id);
        if (bar != null) bar.setStyle(style);
    }

    // -------------------- Animation --------------------

    /**
     * Smoothly animates a boss bar’s progress from one value to another over a set duration.
     *
     * @param id            ID of the boss bar to animate.
     * @param from          Starting progress (0.0–1.0).
     * @param to            Target progress (0.0–1.0).
     * @param durationSec   Total duration of the animation, in seconds.
     * @param intervalSec   Update interval, in seconds.
     * @param plugin        Plugin instance used for scheduling the animation.
     */
    public static void animateProgress(String id, double from, double to, double durationSec, double intervalSec, Plugin plugin) {
        BossBar bar = bossBars.get(id);
        if (bar == null || plugin == null) return;

        stopAnimation(id);

        double steps = Math.max(1, durationSec / intervalSec);
        double delta = (to - from) / steps;

        BukkitRunnable task = new BukkitRunnable() {
            double current = from;

            @Override
            public void run() {
                if ((delta > 0 && current >= to) || (delta < 0 && current <= to)) {
                    bar.setProgress(clamp(to));
                    cancel();
                    activeAnimations.remove(id);
                    return;
                }

                current += delta;
                bar.setProgress(clamp(current));
            }
        };

        task.runTaskTimer(plugin, 0L, Math.max(1L, (long) (intervalSec * 20)));
        activeAnimations.put(id, task);
    }

    /**
     * Creates a looping or one-way loading animation on an existing boss bar.
     *
     * @param id            ID of the boss bar to animate.
     * @param durationSec   Total duration of one fill cycle, in seconds.
     * @param intervalSec   Update interval, in seconds.
     * @param loop          Whether the animation should repeat indefinitely.
     * @param reverse       Whether to start filled and drain instead of fill.
     * @param plugin        Plugin instance used for scheduling the animation.
     */
    public static void animateLoading(String id, double durationSec, double intervalSec, boolean loop, boolean reverse, Plugin plugin) {
        BossBar bar = bossBars.get(id);
        if (bar == null) return;

        stopAnimation(id);

        double steps = durationSec / intervalSec;
        double delta = 1.0 / steps * (reverse ? -1 : 1);

        BukkitRunnable task = new BukkitRunnable() {
            double progress = reverse ? 1.0 : 0.0;

            @Override
            public void run() {
                if ((!reverse && progress >= 1.0) || (reverse && progress <= 0.0)) {
                    if (loop) {
                        progress = reverse ? 1.0 : 0.0;
                    } else {
                        cancel();
                        activeAnimations.remove(id);
                        return;
                    }
                }

                bar.setProgress(clamp(progress));
                progress += delta;
            }
        };

        task.runTaskTimer(plugin, 0L, Math.max(1L, (long) (intervalSec * 20)));
        activeAnimations.put(id, task);
    }

    /**
     * Animates a timed progress bar that fills up over a set duration,
     * optionally running a callback when the animation completes.
     *
     * @param id            ID of the boss bar to animate.
     * @param durationSec   Total duration for the bar to fill, in seconds.
     * @param intervalSec   Update interval, in seconds.
     * @param onComplete    Runnable callback executed when the bar finishes filling (nullable).
     * @param plugin        Plugin instance used for scheduling the animation.
     */
    public static void animateTimedBar(String id, double durationSec, double intervalSec, Runnable onComplete, Plugin plugin) {
        BossBar bar = bossBars.get(id);
        if (bar == null || plugin == null) return;

        stopAnimation(id);

        double steps = Math.max(1, durationSec / intervalSec);
        double delta = 1.0 / steps;

        BukkitRunnable task = new BukkitRunnable() {
            double progress = 0.0;

            @Override
            public void run() {
                if (progress >= 1.0) {
                    bar.setProgress(1.0);
                    cancel();
                    activeAnimations.remove(id);

                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                bar.setProgress(clamp(progress));
                progress += delta;
            }
        };

        task.runTaskTimer(plugin, 0L, Math.max(1L, (long) (intervalSec * 20)));
        activeAnimations.put(id, task);
    }

    /**
     * Stops any active animation running on the specified boss bar.
     *
     * @param id  ID of the boss bar whose animation should be stopped.
     */
    public static void stopAnimation(String id) {
        BukkitRunnable existing = activeAnimations.remove(id);
        if (existing != null) existing.cancel();
    }

    // -------------------- Removal --------------------

    /**
     * Removes the boss bar entirely and clears its associated animation.
     *
     * @param id  ID of the boss bar to remove.
     */
    public static void removeBossBar(String id) {
        stopAnimation(id);
        BossBar bar = bossBars.remove(id);
        if (bar != null) bar.removeAll();
    }

    // -------------------- Retrieval --------------------

    /**
     * Checks if a boss bar exists with the given ID.
     *
     * @param id  ID to check for.
     * @return true if the boss bar exists, false otherwise.
     */
    public static boolean hasBar(String id) {
        return bossBars.containsKey(id);
    }

    /**
     * Retrieves the boss bar associated with the given ID.
     *
     * @param id  ID of the boss bar.
     * @return An Optional containing the BossBar if it exists.
     */
    public static Optional<BossBar> getBar(String id) {
        return Optional.ofNullable(bossBars.get(id));
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(value, 1));
    }
}
