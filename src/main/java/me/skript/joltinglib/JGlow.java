package me.skript.joltinglib;

import fr.skytasul.glowingentities.GlowingBlocks;
import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class JGlow {

    private final GlowingEntities glowingEntities;
    private final GlowingBlocks glowingBlocks;

    public JGlow(JavaPlugin plugin) {
        this.glowingEntities = new GlowingEntities(plugin);
        this.glowingBlocks = new GlowingBlocks(plugin);
    }

    /**
     * Disables glowing entities, must be called when the plugin is shutting down
     */
    public void disable() {
        glowingEntities.disable();
    }

    /**
     * Adds a glowing effect to a target player for a specific viewer with a defined color
     *
     * @param target the player to apply the glowing effect to
     * @param viewer the player who will see the glowing effect
     * @param color the color of the glowing effect
     */
    public void addGlowToPlayer(Player target, Player viewer, ChatColor color) {
        try {
            glowingEntities.setGlowing(target, viewer, color);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a glowing effect to a target player for a specific viewer with a defined color
     * and removes the glow after the given duration
     *
     * @param target the player to apply the glowing effect to
     * @param viewer the player who will see the glowing effect
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the glowing effect will last
     */
    public void addGlowToPlayer(Player target, Player viewer, ChatColor color, long duration) {
        try {
            glowingEntities.setGlowing(target, viewer, color);

            // Schedule the removal of the glow effect after the specified duration
            Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), () -> {
                try {
                    glowingEntities.unsetGlowing(target, viewer);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }, duration * 20);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a glowing effect to a target player for a list of viewers with a defined color
     *
     * @param target the player to apply the glowing effect to
     * @param viewers the list of players who will see the glowing effect
     * @param color the color of the glowing effect
     */
    public void addGlowToPlayer(Player target, List<Player> viewers, ChatColor color) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                addGlowToPlayer(target, viewer, color);
            }
        }
    }

    /**
     * Adds a glowing effect to a target player for a list of viewers with a defined color
     * and removes the glow after the given duration
     *
     * @param target the player to apply the glowing effect to
     * @param viewers the list of players who will see the glowing effect
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the glowing effect will last
     */
    public void addGlowToPlayer(Player target, List<Player> viewers, ChatColor color, long duration) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                addGlowToPlayer(target, viewer, color, duration);
            }
        }
    }

    /**
     * Adds a glowing effect to all online players in the given list for each other
     *
     * @param players the list of players to make glow for each other
     * @param color the color of the glowing effect
     */
    public void addGlowToPlayers(List<Player> players, ChatColor color) {
        for (Player player1 : players) {
            if (!player1.isOnline()) {
                continue;
            }
            for (Player player2 : players) {
                if (player2.isOnline() && !player1.equals(player2)) {
                    addGlowToPlayer(player1, player2, color);
                }
            }
        }
    }

    /**
     * Adds a glowing effect to all online players in the given list for each other
     * with a defined color, and removes the glow after the given duration
     *
     * @param players the list of players to make glow for each other
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the glowing effect will last
     */
    public void addGlowToPlayers(List<Player> players, ChatColor color, long duration) {
        for (Player player1 : players) {
            if (!player1.isOnline()) {
                continue;
            }
            for (Player player2 : players) {
                if (player2.isOnline() && !player1.equals(player2)) {
                    addGlowToPlayer(player1, player2, color, duration);
                }
            }
        }
    }

    /**
     * Removes a glowing effect from a target player for a specific viewer
     *
     * @param target the player to remove the glowing effect from
     * @param viewer the player who will no longer see the glowing effect
     */
    public void removeGlowFromPlayer(Player target, Player viewer) {
        try {
            glowingEntities.unsetGlowing(target, viewer);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a glowing effect from a target player for a list of viewers
     *
     * @param target the player to remove the glowing effect from
     * @param viewers the list of players who will no longer see the glowing effect
     */
    public void removeGlowFromPlayer(Player target, List<Player> viewers) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                removeGlowFromPlayer(target, viewer);
            }
        }
    }

    /**
     * Removes the glowing effect for all online players in the given list
     *
     * @param players the list of players to remove glowing effects from
     */
    public void removeGlowFromPlayers(List<Player> players) {
        for (Player player1 : players) {
            if (!player1.isOnline()) {
                continue;
            }
            for (Player player2 : players) {
                if (player2.isOnline() && !player1.equals(player2)) {
                    removeGlowFromPlayer(player1, player2);
                }
            }
        }
    }

    //////////////-----------------------------------------------------BLOCKS---------------------------------------------------------------///////////

    /**
     * Makes the {@link Block} passed as a parameter glow with the specified color
     * for a specific player
     *
     * @param block the block to make glow
     * @param receiver the player who will see the block glowing
     * @param color the color of the glowing effect
     */
    public void addGlowToBlock(Block block, Player receiver, ChatColor color) {
        try {
            if (isSolidBlockWithVisibleTexture(block)) {
                glowingBlocks.setGlowing(block, receiver, color);
            } else {
                receiver.sendMessage("This block cannot glow because it is either not solid or has no visible texture.");
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes the {@link Block} glow for a list of players with the specified color
     *
     * @param block the block to make glow
     * @param viewers the list of players who will see the block glowing
     * @param color the color of the glowing effect
     */
    public void addGlowToBlock(Block block, List<Player> viewers, ChatColor color) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                // Check if the block is solid and has a visible texture
                if (isSolidBlockWithVisibleTexture(block)) {
                    addGlowToBlock(block, viewer, color);
                } else {
                    viewer.sendMessage("This block cannot glow because it is either not solid or has no visible texture.");
                }
            }
        }
    }

    /**
     * Makes a list of blocks glow for all players in a list with the specified color
     *
     * @param blocks the list of blocks to make glow
     * @param viewers the list of players who will see the blocks glowing
     * @param color the color of the glowing effect
     */
    public void addGlowToBlocks(List<Block> blocks, List<Player> viewers, ChatColor color) {
        for (Block block : blocks) {
            if (block.getWorld() != null && isSolidBlockWithVisibleTexture(block)) {
                addGlowToBlock(block, viewers, color);
            } else {
                for (Player viewer : viewers) {
                    viewer.sendMessage("A block cannot glow because it is either not solid or has no visible texture.");
                }
            }
        }
    }

    /**
     * Makes the {@link Block} passed as a parameter glow with the specified color
     * for a specific player, and removes the glow after the specified duration
     *
     * @param block the block to make glow
     * @param receiver the player who will see the block glowing
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the block will glow
     */
    public void addGlowToBlock(Block block, Player receiver, ChatColor color, long duration) {
        try {
            // Check if the block is solid and has a visible texture
            if (isSolidBlockWithVisibleTexture(block)) {
                // Make the block glow for the player
                glowingBlocks.setGlowing(block, receiver, color);

                // Schedule the removal of the glow after the specified duration
                Bukkit.getScheduler().runTaskLater(JoltingLib.getInstance(), () -> {
                    try {
                        glowingBlocks.unsetGlowing(block, receiver);  // Remove the glow after the duration
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }, duration * 20);
            } else {
                receiver.sendMessage("This block cannot glow because it is either not solid or has no visible texture.");
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes the {@link Block} glow for a list of players with a specified color
     * and removes the glow after the given duration
     *
     * @param block the block to make glow
     * @param viewers the list of players who will see the block glowing
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the block will glow
     */
    public void addGlowToBlock(Block block, List<Player> viewers, ChatColor color, long duration) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                if (isSolidBlockWithVisibleTexture(block)) {
                    addGlowToBlock(block, viewer, color, duration);
                } else {
                    viewer.sendMessage("This block cannot glow because it is either not solid or has no visible texture.");
                }
            }
        }
    }

    /**
     * Makes a list of blocks glow for all players in a list with the specified color
     * and removes the glow after the given duration
     *
     * @param blocks the list of blocks to make glow
     * @param viewers the list of players who will see the blocks glowing
     * @param color the color of the glowing effect
     * @param duration the duration (in seconds) for which the blocks will glow
     */
    public void addGlowToBlocks(List<Block> blocks, List<Player> viewers, ChatColor color, long duration) {
        for (Block block : blocks) {
            if (block.getWorld() != null && isSolidBlockWithVisibleTexture(block)) {
                addGlowToBlock(block, viewers, color, duration);
            } else {
                for (Player viewer : viewers) {
                    viewer.sendMessage("A block cannot glow because it is either not solid or has no visible texture.");
                }
            }
        }
    }

    /**
     * Checks if the block is solid and has a visible texture.
     *
     * @param block The block to check
     * @return true if the block is solid and has a visible texture
     */
    private boolean isSolidBlockWithVisibleTexture(Block block) {
        Material type = block.getType();

        if (type.isSolid()) {
            return type != Material.GLASS && type != Material.AIR && type != Material.WATER && type != Material.LAVA && type != Material.BARRIER;
        }
        return false;
    }

    /**
     * Removes the glowing effect from the {@link Block} for a specific player
     *
     * @param block the block to remove the glowing effect from
     * @param receiver the player who will no longer see the glowing effect
     */
    public void removeGlowFromBlock(Block block, Player receiver) {
        try {
            glowingBlocks.unsetGlowing(block, receiver);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the glowing effect from the {@link Block} for a list of players
     *
     * @param block the block to remove the glowing effect from
     * @param viewers the list of players who will no longer see the glowing effect
     */
    public void removeGlowFromBlock(Block block, List<Player> viewers) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) {
                removeGlowFromBlock(block, viewer);
            }
        }
    }

    /**
     * Removes the glowing effect from a list of blocks for a list of players
     *
     * @param blocks the list of blocks to remove the glowing effect from
     * @param viewers the list of players who will no longer see the glowing effect
     */
    public void removeGlowFromBlocks(List<Block> blocks, List<Player> viewers) {
        for (Block block : blocks) {
            if (block.getWorld() != null) {
                removeGlowFromBlock(block, viewers);
            }
        }
    }
}
