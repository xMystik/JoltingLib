package me.skript.joltinglib;

import me.skript.joltinglib.colorcodes.JText;
import me.skript.joltinglib.configurations.JYML;
import me.skript.joltinglib.configurations.JFilesManager;
import me.skript.joltinglib.glow.JGlow;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class JoltingLib extends JavaPlugin {

    private static JoltingLib instance;
    private final JFilesManager filesManager = new JFilesManager<>(this);
    private JYML configurationFile;
    private JGlow glowManager;

    @Override
    public void onEnable() {
        instance = this;
        this.glowManager = new JGlow(this);

        this.configurationFile = filesManager.createYML("configuration");

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
}
