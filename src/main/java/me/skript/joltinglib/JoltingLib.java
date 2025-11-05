package me.skript.joltinglib;

import me.skript.joltinglib.configurations.JYML;
import me.skript.joltinglib.configurations.JFilesManager;
import me.skript.joltinglib.glow.JGlow;
import me.skript.joltinglib.placeholders.Placeholders;
import me.skript.joltinglib.text.JText;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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

        this.getLogger().log(Level.INFO, "[JoltingLib] Library has been enabled!");

        // Placeholders
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Placeholders(this).register();

            Bukkit.getConsoleSender().sendMessage(JText.format("<dark_aqua><bold>[JoltingLib]</bold> <gray>Placeholders have been registered!"));
        }
        else {
            Bukkit.getConsoleSender().sendMessage(JText.format("<dark_red><bold>[JoltingLib]</bold> <gray>Could not register placeholders!"));
        }
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
