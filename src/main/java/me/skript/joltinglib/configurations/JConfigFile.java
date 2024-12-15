package me.skript.joltinglib.configurations;

import me.skript.joltinglib.JoltingLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JConfigFile<P extends Plugin> {

    private final P plugin;
    private final File file;
    private final FileConfiguration config;
    private final String fileAsName;
    private final String folderPath;

    protected JConfigFile(P plugin, String fileName) {
        this(plugin, fileName, "");
    }

    protected JConfigFile(P plugin, String fileName, String folderPathName) {
        this.plugin = plugin;
        this.fileAsName = fileName;
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

    public File getConfigFile() {
        return file;
    }

    public String getFileAsName() {
        return fileAsName;
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
            JoltingLib.getInstance().getLogger().severe("Failed to save " + fileAsName + " file. Error: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            JoltingLib.getInstance().getLogger().severe("Failed to reload " + fileAsName + " file. Error: " + e.getMessage());
        }
    }

    protected void copyDefaults(InputStream inputFile) {
        try {
            Files.copy(inputFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JoltingLib.getInstance().getLogger().severe("Failed to copy default file for " + fileAsName + ". Error: " + e.getMessage());
        }
    }

    private void setupConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            InputStream defaultFile = plugin.getResource(fileAsName + ".yml");
            if (defaultFile != null) {
                copyDefaults(defaultFile);
            } else {
                try {
                    if (file.createNewFile()) {
                        JoltingLib.getInstance().getLogger().info("Created empty configuration file: " + fileAsName + ".yml");
                    }
                } catch (IOException e) {
                    JoltingLib.getInstance().getLogger().severe("Failed to create empty file for " + fileAsName + ". Error: " + e.getMessage());
                }
            }
        }
    }
}