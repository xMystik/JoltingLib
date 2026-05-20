package me.skript.joltinglib.utilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Shared teleport helper for plugins that need predictable player teleports.
 */
public final class JTeleport {

    private JTeleport() {
    }

    public static boolean teleport(Player player, Location location) {
        if (player == null || location == null || location.getWorld() == null) {
            return false;
        }

        prepare(player);
        boolean result = player.teleport(location);

        if (!result && isAtLocation(player, location)) {
            return true;
        }

        return result || player.teleport(location);
    }

    public static CompletableFuture<Boolean> teleportAsync(Player player, Location location) {
        if (player == null || location == null || location.getWorld() == null) {
            return CompletableFuture.completedFuture(false);
        }

        prepare(player);
        return player.teleportAsync(location);
    }

    public static void prepare(Player player) {
        if (player.isSleeping()) {
            player.wakeup(false);
        }

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }
    }

    private static boolean isAtLocation(Player player, Location location) {
        Location current = player.getLocation();
        if (current.getWorld() == null || !current.getWorld().equals(location.getWorld())) {
            return false;
        }

        return current.distanceSquared(location) <= 0.0001D;
    }
}
