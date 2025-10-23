package me.skript.joltinglib;

import me.skript.joltinglib.bossbars.JBossBar;
import me.skript.joltinglib.configurations.JYML;
import me.skript.joltinglib.configurations.JFilesManager;
import me.skript.joltinglib.glow.JGlow;
import me.skript.joltinglib.text.JText;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class JoltingLib extends JavaPlugin implements Listener {

    private static JoltingLib instance;
    private JFilesManager filesManager = new JFilesManager<>(this);
    private JYML configurationFile;
    private JGlow glowManager;

    @Override
    public void onEnable() {
        instance = this;
        this.glowManager = new JGlow(this);

        this.configurationFile = filesManager.createYML("configuration");

        this.getLogger().log(Level.INFO, "[JoltingLib] Library has been enabled!");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        glowManager.disable();

        this.getLogger().log(Level.INFO, "[JoltingLib] Library has been disabled!");
    }

    public static JoltingLib getInstance() {
        return instance;
    }

    public FileConfiguration getConfigurationFile() {
        return configurationFile.getConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        JBossBar.createBossBar("testbar", "<#ff00c3><bold>Really Cool Queue Bar", BarColor.PINK, BarStyle.SOLID);

        JBossBar.showBossBar("testbar", player);

        player.sendMessage(JText.formatLegacy("<yellow>&lGeia sou!"));

        switch (event.getBlock().getType()) {
            case DIAMOND_BLOCK -> JBossBar.setTitle("testbar", "&6<bold>GEIA");
            case GOLD_BLOCK -> JBossBar.animateProgress("testbar", 0.0, 1.0, 5, 0.5, this);
            case EMERALD_BLOCK -> JBossBar.animateProgress("testbar", 1.0, 0.0, 3, 2.0, this);
            case BARRIER -> JBossBar.removeBossBar("testbar");
            case BARREL -> JBossBar.animateLoading("testbar",30, 0.5, true, false, this);
            default -> {
                return;
            }
        }
    }
}
