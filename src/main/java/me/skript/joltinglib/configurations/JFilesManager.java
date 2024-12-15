package me.skript.joltinglib.configurations;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class JFilesManager<P extends Plugin> {

    private final P plugin;
    private final Map<String, JConfigFile<P>> filesMap = new HashMap<>();

    public JFilesManager(P plugin) {
        this.plugin = plugin;
    }

    public void createFolder(String folderName, String... folderPath) {
        StringBuilder path = new StringBuilder();
        for (String s : folderPath) {
            path.append("/").append(s);
        }
        File folder = new File(plugin.getDataFolder() + "" + path, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void createFolder(String folderName) {
        createFolder(folderName, "");
    }

    public JConfigFile<P> createFile(String fileName, String folderName) {
        String key = folderName.isEmpty() ? fileName + ".yml" : folderName + "/" + fileName + ".yml";
        return filesMap.computeIfAbsent(key, k -> new JConfigFile<>(plugin, fileName, folderName));
    }

    public JConfigFile<P> createFile(String fileName) {
        String key = fileName + ".yml";
        return filesMap.computeIfAbsent(key, k -> new JConfigFile<>(plugin, fileName));
    }

    public void deleteFile(JConfigFile<P> configFile) {
        if (configFile.getConfigFile().exists() && configFile.getConfigFile().delete()) {
            filesMap.remove(configFile.getFolderPath());
        } else {
            plugin.getLogger().warning("Failed to delete configuration file: " + configFile.getFileName());
        }
    }

    public JConfigFile<P> getFile(String filePath) {
        return filesMap.get(filePath);
    }

    public Map<String, JConfigFile<P>> getAllRegisteredConfigs() {
        return new HashMap<>(filesMap);
    }

    public Map<String, JConfigFile<P>> getAllConfigsInFolder(String folderPath) {
        Map<String, JConfigFile<P>> filteredMap = new HashMap<>();
        filesMap.forEach((key, value) -> {
            if (key.startsWith(folderPath)) {
                filteredMap.put(key, value);
            }
        });
        return filteredMap;
    }

    public Map<String, JConfigFile<P>> discoverConfigs(String folderPath) {
        Map<String, JConfigFile<P>> discoveredConfigs = new HashMap<>();
        File folder = new File(plugin.getDataFolder(), folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory() && file.getName().endsWith(".yml")) {
                    String fileName = file.getName().replace(".yml", "");
                    discoveredConfigs.put(folderPath + "/" + file.getName(), createFile(fileName, folderPath));
                }
            }
        }
        return discoveredConfigs;
    }
}
