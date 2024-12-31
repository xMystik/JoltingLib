package me.skript.joltinglib.configurations;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class JFilesManager<P extends Plugin> {

    private final P plugin;
    private final Map<String, Object> filesMap;

    /**
     * Constructs a new instance of JFilesManager
     *
     * @param plugin the plugin instance using this file manager.
     */
    public JFilesManager(P plugin) {
        this.plugin = plugin;
        this.filesMap = new HashMap<>();
    }

    /**
     * Creates a folder with the specified name and path
     *
     * @param folderName the name of the folder to create
     * @param folderPath the optional path where the folder should be created
     */
    public void createFolder(String folderName, String... folderPath) {
        String folder = String.join("/", folderPath);
        File directory = folder.isEmpty() ? new File(plugin.getDataFolder(), folderName)
                : new File(plugin.getDataFolder() + "/" + folder, folderName);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Creates a folder in the root of the plugin's data folder
     *
     * @param folderName the name of the folder to create
     */
    public void createFolder(String folderName) {
        createFolder(folderName, "");
    }

    /**
     * Deletes a folder and all its contents
     *
     * @param folderName the name of the folder to delete
     * @param folderPath the optional path to the folder
     * @return true if the folder was successfully deleted, false otherwise
     */
    public boolean deleteFolder(String folderName, String... folderPath) {
        String folder = String.join("/", folderPath);
        File directory = folder.isEmpty() ? new File(plugin.getDataFolder(), folderName)
                : new File(plugin.getDataFolder() + "/" + folder, folderName);

        return deleteFolderRecursive(directory);
    }

    /**
     * Recursively deletes a folder and its contents
     *
     * @param directory the folder to delete
     * @return true if deletion was successful, false otherwise
     */
    private boolean deleteFolderRecursive(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderRecursive(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }

    /**
     * Creates or retrieves a YAML configuration file
     *
     * @param fileName the name of the file (without extension)
     * @param folderPath the optional path for the file
     * @return the JYML instance
     */
    public JYML<P> createYML(String fileName, String... folderPath) {
        String key = String.join("/", folderPath) + "/" + fileName + ".yml";
        return (JYML<P>) filesMap.computeIfAbsent(key, k -> new JYML<>(plugin, fileName, folderPath));
    }

    /**
     * Deletes a file by its key
     *
     * @param key the key (path) of the file to delete
     */
    public void deleteFile(String key) {
        Object file = filesMap.remove(key);
        if (file instanceof JYML) {
            ((JYML<?>) file).getFile().delete();
        }
    }

    /**
     * Retrieves a file (actual File object) by its key
     *
     * @param key the key (path) of the file
     * @return the File object, or null if not found
     */
    public File getFile(String key) {
        Object fileObject = filesMap.get(key);
        if (fileObject instanceof JYML) {
            return ((JYML<?>) fileObject).getFile();
        }
        return null;
    }

    /**
     * Retrieves all registered files
     *
     * @return a map of all registered files with their keys
     */
    public Map<String, Object> getAllRegisteredFiles() {
        return new HashMap<>(filesMap);
    }

    /**
     * Retrieves all registered YML files
     *
     * @return a map of all registered YML files with their keys
     */
    public Map<String, JYML<P>> getAllYMLConfigs() {
        Map<String, JYML<P>> yamlFiles = new HashMap<>();
        for (Map.Entry<String, Object> entry : filesMap.entrySet()) {
            if (entry.getValue() instanceof JYML) {
                yamlFiles.put(entry.getKey(), (JYML<P>) entry.getValue());
            }
        }
        return yamlFiles;
    }
}
