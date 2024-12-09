package me.skript.joltinglib.configurations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;

public class JConfigFile<P extends Plugin> {

    private final P plugin;
    private final File file;
    private FileConfiguration config;
    private final String fileAsName;
    private final String folderPath;

    //----------------------------[ ConfigFiles Constructors Start ]----------------------------//
    //Used to create a config file on the default plugins path.
    public JConfigFile(P plugin, String fileName) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), fileName + ".yml");
        fileAsName = fileName;
        folderPath = "default/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }

    //Used to create a config file on the specified path.
    public JConfigFile(P plugin, String fileName, String folderPathName) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder() + "/" + folderPathName, fileName + ".yml");
        fileAsName = fileName;
        folderPath = folderPathName + "/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }

    //Used to create a config file on the specified path(s).
    public JConfigFile(P plugin, String fileName, String... folderPathName) {
        this.plugin = plugin;
        String path = "default";
        for(String s : folderPathName) {
            path += "/" + s;
        }
        file = new File(plugin.getDataFolder() + "/" + path, fileName + ".yml");
        fileAsName = fileName;
        folderPath = path + "/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }
    //-----------------------------[ ConfigFiles Constructors End ]-----------------------------//

    //------------------------------[ ConfigFiles Methods Start ]-------------------------------//
    //Used to return the configuration the file.
    public FileConfiguration getConfig() {
        return config;
    }

    //Used to return the file.
    public File getConfigFile() {
        return file;
    }

    //Used to return the file name.
    public String getFileAsName() {
        return fileAsName;
    }

    //Used to return the file name. (Including the .yml)
    public String getFileName() { return file.getName(); }

    //Used to return the folder path.
    public String getFolderPath() { return folderPath; }

    //Used to save the configuration file.
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage( fileAsName + " file saving has been cancelled. [IOException]");
        }
    }

    //Used to load file contents into the file configuration.
    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(fileAsName + " file reloading has been cancelled. [Exception]");
        }
    }

    public void copyDefaults(InputStream inputFile, File outputFile) {
        try (OutputStream output = Files.newOutputStream(outputFile.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputFile.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Copy defaults has been cancelled for " + outputFile.getName() + " [Exception: " + e.getMessage() + "]");
            e.printStackTrace();
        } finally {
            try {
                if (inputFile != null) inputFile.close();
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("Failed to close input stream. [Exception: " + e.getMessage() + "]");
            }
        }
    }

    private void setupConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            InputStream inputStream = plugin.getResource(fileAsName + ".yml");
            if (inputStream != null) {
                copyDefaults(inputStream, file);
            } else {
                Bukkit.getConsoleSender().sendMessage("File for " + fileAsName + ".yml not found in the jar.");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    //-------------------------------[ ConfigFiles Methods End ]--------------------------------//
}