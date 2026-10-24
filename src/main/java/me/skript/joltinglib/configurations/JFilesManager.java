package me.skript.joltinglib.configurations;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.jar.JarFile;

public class JFilesManager<P extends Plugin> {

    private final P plugin;
    private final Map<String, Object> filesMap;

    /**
     * Constructs a new instance of JFilesManager
     *
     * @param plugin the plugin instance using this file manager
     */
    public JFilesManager(P plugin) {
        this.plugin = plugin;
        this.filesMap = new HashMap<>();
    }

    /**
     * Copies a bundled resource folder (including subfolders) to the same relative
     * location in this plugin's data folder. Existing files are never overwritten.
     * For example, "Menus" copies resources/Menus/** to plugins/YourPlugin/Menus/**.
     * Safe to call on every startup; newly bundled files are added automatically.
     * @return number of new files copied (not directories)
     * @throws IllegalArgumentException if the resource path is invalid or missing
     * @throws IllegalStateException if the resources cannot be copied
     */
    public int saveResourceFolder(String resourceFolder) {
        try {
            Path source = Paths.get(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            return copyResourceFolder(source, plugin.getDataFolder().toPath(), resourceFolder);
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Could not copy resource folder '" + resourceFolder + "'", exception);
        }
    }

    // Package-private so real JAR extraction can be tested without a running server.
    static int copyResourceFolder(Path source, Path destination, String folder) throws IOException {
        Objects.requireNonNull(folder, "resourceFolder");
        folder = folder.replace('\\', '/').replaceAll("/+$", "");
        if (folder.isBlank() || folder.startsWith("/") || folder.contains(":"))
            throw new IllegalArgumentException("Use a non-empty relative resource folder");
        for (String segment : folder.split("/", -1)) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals(".."))
                throw new IllegalArgumentException("Invalid resource folder: " + folder);
        }
        Path root = destination.toAbsolutePath().normalize();
        String prefix = folder + "/";
        int copied = 0;
        boolean found = false;
        if (Files.isDirectory(source)) {
            Path resourceRoot = source.resolve(folder);
            if (!Files.isDirectory(resourceRoot)) throw new IllegalArgumentException("Missing resource folder: " + folder);
            try (var paths = Files.walk(resourceRoot)) {
                for (Path path : paths.toList()) {
                    if (Files.isSymbolicLink(path)) throw new IOException("Symbolic links are not supported in resources: " + path);
                    String name = source.relativize(path).toString().replace(File.separatorChar, '/');
                    Path target = resourceTarget(root, name);
                    if (Files.isDirectory(path)) Files.createDirectories(target);
                    else try (InputStream input = Files.newInputStream(path)) { copied += copyDefault(input, target); }
                }
            }
            return copied;
        }
        try (JarFile jar = new JarFile(source.toFile())) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.getName().startsWith(prefix)) continue;
                found = true;
                Path target = resourceTarget(root, entry.getName());
                if (entry.isDirectory()) Files.createDirectories(target);
                else try (InputStream input = jar.getInputStream(entry)) { copied += copyDefault(input, target); }
            }
        }
        if (!found) throw new IllegalArgumentException("Missing resource folder: " + folder);
        return copied;
    }

    private static Path resourceTarget(Path root, String name) throws IOException {
        if (name.contains("\\") || name.contains(":")) throw new IOException("Invalid resource entry: " + name);
        for (String segment : name.split("/")) {
            if (segment.equals("..") || segment.equals(".")) throw new IOException("Invalid resource entry: " + name);
        }
        Path target = root.resolve(name).normalize();
        if (!target.startsWith(root)) throw new IOException("Resource escapes the data folder: " + name);
        for (Path path = target; path != null; path = path.getParent()) {
            if (Files.isSymbolicLink(path)) throw new IOException("Refusing to copy through a symbolic link: " + path);
        }
        return target;
    }

    private static int copyDefault(InputStream input, Path target) throws IOException {
        if (Files.isDirectory(target)) throw new IOException("A directory occupies the resource file path: " + target);
        Files.createDirectories(target.getParent());
        try { Files.copy(input, target); return 1; }
        catch (FileAlreadyExistsException ignored) { return 0; }
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
