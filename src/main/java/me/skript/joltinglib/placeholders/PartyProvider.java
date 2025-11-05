package me.skript.joltinglib.placeholders;

import org.bukkit.entity.Player;

public interface PartyProvider {
    boolean areInSameParty(Player target, Player viewer);
}
