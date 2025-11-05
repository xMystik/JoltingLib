package me.skript.joltinglib.placeholders;

import me.skript.joltinglib.JDebug;
import org.bukkit.entity.Player;

public class RelationManager {

    private static PartyProvider partyProvider;
    private static PvPProvider pvpProvider;
    private static FriendsProvider friendsProvider;

    // ===== Registration =====
    public static void setPartyProvider(PartyProvider provider) {
        JDebug.log("[JoltingLib] Party provider registered: " + provider.getClass().getSimpleName());
        partyProvider = provider;
    }

    public static void setFriendsProvider(FriendsProvider provider) {
        JDebug.log("[JoltingLib] Friends provider registered: " + provider.getClass().getSimpleName());
        friendsProvider = provider;
    }

    public static void setPvPProvider(PvPProvider provider) {
        JDebug.log("[JoltingLib] PvP provider registered: " + provider.getClass().getSimpleName());
        pvpProvider = provider;
    }

    // ===== Access =====
    public static boolean areInParty(Player p1, Player p2) {
        return partyProvider != null && partyProvider.areInSameParty(p1, p2);
    }

    public static boolean areFriends(Player p1, Player p2) {
        return friendsProvider != null && friendsProvider.areFriends(p1, p2);
    }

    public static boolean hasPvPActive(Player player) {
        return pvpProvider != null && pvpProvider.hasPvPActive(player);
    }
}
