package me.skript.joltinglib.bossbars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JBossBar implements Listener {

    private final Plugin plugin;
    private final Map<String, BossBar> bossBars = new HashMap<>();

    public JBossBar(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            bossBars.values().forEach(BossBar::removeAll);
            bossBars.clear();
        }
    }

    public BossBar createBossBar(String id, String title, BarColor color, BarStyle style) {
        if (bossBars.containsKey(id)) {
            BossBar existingBar = bossBars.get(id);
            if (!existingBar.getTitle().equals(title) || !existingBar.getColor().equals(color) || !existingBar.getStyle().equals(style)) {
                throw new IllegalArgumentException("Boss bar " + id + " already exists with different parameters");
            }
            return existingBar;
        }
        return bossBars.computeIfAbsent(id, k -> Bukkit.createBossBar(title, color, style));
    }

    public BossBar createBossBar(String id, String title, BarColor color, BarStyle style, BarFlag... flags) {
        if (bossBars.containsKey(id)) {
            BossBar existingBar = bossBars.get(id);
            if (!existingBar.getTitle().equals(title) || !existingBar.getColor().equals(color) || !existingBar.getStyle().equals(style)) {
                throw new IllegalArgumentException("Boss bar " + id + " already exists with different parameters");
            }
            return existingBar;
        }
        return bossBars.computeIfAbsent(id, k -> Bukkit.createBossBar(title, color, style, flags));
    }

    public void removeBossBar(String id) {
        BossBar bar = bossBars.remove(id);
        if (bar != null) {
            bar.removeAll();
        }
    }

    public void showBossBar(String id, Player player) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.addPlayer(player);
        }
    }

    public void showToAll(String id) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
        }
    }

    public void hideBossBar(String id, Player player) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.removePlayer(player);
        }
    }

    public void hideFromAll(String id) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            Bukkit.getOnlinePlayers().forEach(bar::removePlayer);
        }
    }

    public void updateTitle(String id, String title) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.setTitle(title);
        }
    }

    public void setColor(String id, BarColor color) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.setColor(color);
        }
    }

    public void setStyle(String id, BarStyle style) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.setStyle(style);
        }
    }

    public void setProgress(String id, double progress) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.setProgress(Math.max(0, Math.min(progress, 1))); // Clamp between 0 and 1
        }
    }

    public void addFlag(String id, BarFlag flag) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.addFlag(flag);
        }
    }

    public void removeFlag(String id, BarFlag flag) {
        BossBar bar = bossBars.get(id);
        if (bar != null) {
            bar.removeFlag(flag);
        }
    }

    public boolean hasBar(String id) {
        return bossBars.containsKey(id);
    }

    public Optional<BossBar> getBar(String id) {
        return Optional.ofNullable(bossBars.get(id));
    }

}
