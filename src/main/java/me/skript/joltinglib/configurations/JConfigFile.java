package me.skript.joltinglib.configurations;

import me.skript.joltinglib.JDebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class JConfigFile<P extends Plugin> {

    private final P plugin;
    private final File file;
    private final FileConfiguration config;
    private final String fileName;
    private final String folderPath;

    protected JConfigFile(P plugin, String fileName) {
        this(plugin, fileName, "");
    }

    protected JConfigFile(P plugin, String fileName, String folderPathName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.folderPath = folderPathName.isEmpty() ? fileName + ".yml" : folderPathName + "/" + fileName + ".yml";
        this.file = folderPathName.isEmpty() ?
                new File(plugin.getDataFolder(), fileName + ".yml") :
                new File(plugin.getDataFolder() + "/" + folderPathName, fileName + ".yml");

        setupConfig();
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return fileName;
    }

    public String getFileName() {
        return file.getName();
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            JDebug.log(Level.SEVERE, "Failed to save " + fileName + " file. Error: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            JDebug.log(Level.SEVERE, "Failed to reload " + fileName + " file. Error: " + e.getMessage());
        }
    }

    protected void copyDefaults(InputStream inputFile) {
        try {
            Files.copy(inputFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JDebug.log(Level.SEVERE, "Failed to copy default file for " + fileName + ". Error: " + e.getMessage());
        }
    }

    protected void setupConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            InputStream defaultFile = plugin.getResource(fileName + ".yml");

            if (defaultFile != null) {
                copyDefaults(defaultFile);
            } else {
                try {
                    if (file.createNewFile()) {
                        JDebug.log(Level.INFO, "Created empty configuration file: " + fileName + ".yml");
                    }
                } catch (IOException e) {
                    JDebug.log(Level.SEVERE, "Failed to create empty file for " + fileName + ". Error: " + e.getMessage());
                }
            }
        }
    }
}