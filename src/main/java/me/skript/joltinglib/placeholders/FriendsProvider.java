package me.skript.joltinglib.placeholders;

import org.bukkit.entity.Player;

public interface FriendsProvider {
    boolean areFriends(Player target, Player viewer);
}
