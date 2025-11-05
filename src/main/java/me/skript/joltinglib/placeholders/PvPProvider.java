package me.skript.joltinglib.placeholders;

import org.bukkit.entity.Player;

public interface PvPProvider {
    boolean hasPvPActive(Player target);
}
