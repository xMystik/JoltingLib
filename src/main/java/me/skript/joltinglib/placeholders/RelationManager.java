package me.skript.joltinglib.placeholders;

import me.skript.joltinglib.JDebug;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RelationManager {

    private static PartyProvider partyProvider;
    private static PvPProvider pvpProvider;
    private static FriendsProvider friendsProvider;

    // Prevent log spam (log only once per missing provider type)
    private static boolean loggedPartyMissing = false;
    private static boolean loggedFriendsMissing = false;
    private static boolean loggedPvPMissing = false;

    // ===== Registration =====

    public static void setPartyProvider(PartyProvider provider) {
        partyProvider = provider;
        loggedPartyMissing = false;
    }

    public static void setFriendsProvider(FriendsProvider provider) {
        friendsProvider = provider;
        loggedFriendsMissing = false;
    }

    public static void setPvPProvider(PvPProvider provider) {
        pvpProvider = provider;
        loggedPvPMissing = false;
    }

    // ===== Access =====

    public static boolean areInParty(Player p1, Player p2) {
        if (partyProvider == null) {
            logOnce("Party");
            return false;
        }
        try {
            return partyProvider.areInSameParty(p1, p2);
        } catch (Exception ex) {
            JDebug.log("[JoltingLib] Error while checking party relation: " + ex.getMessage());
            return false;
        }
    }

    public static boolean areFriends(Player p1, Player p2) {
        if (friendsProvider == null) {
            logOnce("Friends");
            return false;
        }
        try {
            return friendsProvider.areFriends(p1, p2);
        } catch (Exception ex) {
            JDebug.log("[JoltingLib] Error while checking friends relation: " + ex.getMessage());
            return false;
        }
    }

    public static boolean hasPvPActive(Player player) {
        if (pvpProvider == null) {
            logOnce("PvP");
            return false;
        }
        try {
            return pvpProvider.hasPvPActive(player);
        } catch (Exception ex) {
            JDebug.log("[JoltingLib] Error while checking PvP state: " + ex.getMessage());
            return false;
        }
    }

    // ===== Internal Helpers =====

    private static void logOnce(String type) {
        switch (type) {
            case "Party" -> {
                if (!loggedPartyMissing) {
                    JDebug.log("[JoltingLib] No Party provider registered!");
                    loggedPartyMissing = true;
                }
            }
            case "Friends" -> {
                if (!loggedFriendsMissing) {
                    JDebug.log("[JoltingLib] No Friends provider registered!");
                    loggedFriendsMissing = true;
                }
            }
            case "PvP" -> {
                if (!loggedPvPMissing) {
                    JDebug.log("[JoltingLib] No PvP provider registered!");
                    loggedPvPMissing = true;
                }
            }
        }
    }

    public static void logProviderStatus() {
        Bukkit.getLogger().info("[JoltingLib] RelationManager provider status:");
        Bukkit.getLogger().info("  Party: " + (partyProvider != null));
        Bukkit.getLogger().info("  Friends: " + (friendsProvider != null));
        Bukkit.getLogger().info("  PvP: " + (pvpProvider != null));
    }
}
