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

    protected final P plugin;
    protected final File file;
    protected final FileConfiguration config;

    /**
     * Constructs a new JYML instance
     *
     * @param plugin the plugin instance using this YAML file
     * @param fileName the name of the YAML file (without extension)
     * @param folderPath the folder path relative to the plugin's data folder
     */
    protected JYML(P plugin, String fileName, String... folderPath) {
        this.plugin = plugin;
        String folder = String.join("/", folderPath);
        this.file = folder.isEmpty() ? new File(plugin.getDataFolder(), fileName + ".yml")
                : new File(plugin.getDataFolder() + "/" + folder, fileName + ".yml");
        setupFile(fileName);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Retrieves the configuration object for this YAML file
     *
     * @return the FileConfiguration object
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Retrieves the file object for this YAML file
     *
     * @return the File object representing the YAML file
     */
    public File getFile() {
        return file;
    }

    /**
     * Saves changes made to the configuration back to the YAML file
     */
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            JDebug.log(Level.SEVERE, "Failed to save YAML file: " + file.getName() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Reloads the YAML file from disk, updating the configuration object
     */
    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            JDebug.log(Level.SEVERE, "Failed to reload YAML file: " + file.getName() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Sets up the YAML file by ensuring its existence
     *
     * @param fileName the name of the YAML file
     */
    protected void setupFile(String fileName) {
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