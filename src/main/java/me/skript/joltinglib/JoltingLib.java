package me.skript.joltinglib;

import me.skript.joltinglib.bossbars.JBossBar;
import me.skript.joltinglib.configurations.JYML;
import me.skript.joltinglib.configurations.JFilesManager;
import me.skript.joltinglib.glow.JGlow;
import me.skript.joltinglib.holograms.JHologramBuilder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        // Register the block break hologram listener
        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
                JBossBar.createBossBar(event.getPlayer().getUniqueId().toString(), "My Bossbar", BarColor.GREEN, BarStyle.SOLID);
                JBossBar.showBossBar(event.getPlayer().getUniqueId().toString(), event.getPlayer());

                var block = event.getBlock();
                var location = block.getLocation().add(0.5, 1.5, 0.5);

                String name = block.getType().name().toLowerCase().replace("_", " ");

                new JHologramBuilder(location)
                        .addLine("<#02d63b><bold>" + name + " <dark_red><italic>Test #2")
                        .addLine("<dark_aqua><bold>Another cute line!")
                        .setDuration(100)
                        .setAlignment(TextDisplay.TextAlignment.CENTER)
                        .setFloatHeight(5)
                        .build();

                JBossBar.animateProgress(event.getPlayer().getUniqueId().toString(), 0, 1, 10, 1, JoltingLib.getInstance());
            }

        }, this);

        this.getLogger().log(Level.INFO, "[JoltingLib] Library has been enabled!");
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
}
