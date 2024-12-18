package me.skript.joltinglib.configurations;

import me.skript.joltinglib.JDebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class JYML<P extends Plugin> {

    private final P plugin;
    private final File file;
    private final FileConfiguration config;

    public JYML(P plugin, String fileName, String... folderPath) {
        this.plugin = plugin;
        String folder = String.join("/", folderPath);
        this.file = folder.isEmpty() ? new File(plugin.getDataFolder(), fileName + ".yml")
                : new File(plugin.getDataFolder() + "/" + folder, fileName + ".yml");
        setupFile(fileName);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            JDebug.log(Level.SEVERE, "Failed to save YAML file: " + file.getName() + ". Error: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            JDebug.log(Level.SEVERE, "Failed to reload YAML file: " + file.getName() + ". Error: " + e.getMessage());
        }
    }

    private void setupFile(String fileName) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            InputStream defaultFile = plugin.getResource(fileName + ".yml");

            if (defaultFile != null) {
                try {
                    Files.copy(defaultFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    JDebug.log(Level.SEVERE, "Failed to copy default YAML file: " + file.getName() + ". Error: " + e.getMessage());
                }
            } else {
                try {
                    if (file.createNewFile()) {
                        JDebug.log(Level.INFO, "Created new YAML file: " + file.getName());
                    }
                } catch (IOException e) {
                    JDebug.log(Level.SEVERE, "Failed to create new YAML file: " + file.getName() + ". Error: " + e.getMessage());
                }
            }
        }
    }
}