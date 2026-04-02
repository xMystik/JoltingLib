package me.skript.joltinglib.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkullCache {

    private final Map<UUID, PlayerProfile> cache = new ConcurrentHashMap<>();
    private final Queue<UUID> queue = new ConcurrentLinkedQueue<>();

    public SkullCache(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            int perCycle = 2;

            for (int i = 0; i < perCycle; i++) {
                UUID uuid = queue.poll();
                if (uuid == null) return;

                if (cache.containsKey(uuid)) continue;

                try {
                    PlayerProfile profile = Bukkit.createProfile(uuid);
                    profile.complete(true); // async safe
                    cache.put(uuid, profile);
                } catch (Exception ignored) {}
            }

        }, 20L, 2L); // start after 1s, run every 2 ticks
    }

    public void queue(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            queue.add(uuid);
        }
    }

    public PlayerProfile get(UUID uuid) {
        return cache.get(uuid);
    }

    public void preloadPlayers() {
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            queue(p.getUniqueId());
        }
    }
}
