package me.skript.joltinglib.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.skript.joltinglib.JoltingLib;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion implements Relational {

    private final JoltingLib plugin;

    public Placeholders(JoltingLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "joltinglib";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player target, Player viewer, String identifier) {
        if (target == null || viewer == null) return null;

        if (identifier.equalsIgnoreCase("relation_color")) {
            if (RelationManager.hasPvPActive(target)) {
                return "&c";
            }

            if (RelationManager.areInParty(target, viewer)) {
                return "&e";
            }

            if (RelationManager.areFriends(target, viewer)) {
                return "&b";
            }

            // TODO - ADD GUILD FUNCTIONALITY
//            if (areInGuild(target, viewer)) {
//                return "&2";
//            }

            // TODO - ADD ALLIANCE FUNCTIONALITY
//            if (areInAlliance(target, viewer)) {
//                return "&5";
//            }

            return "&3";
        }
        return null;
    }
}
