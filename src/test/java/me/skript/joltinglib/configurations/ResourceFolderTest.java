package me.skript.joltinglib.configurations;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.jar.*;
import static org.junit.jupiter.api.Assertions.*;

class ResourceFolderTest {
    @TempDir Path temp;

    private Path jar(Map<String, String> entries) throws IOException {
        Path jar = temp.resolve("plugin.jar");
        try (var output = new JarOutputStream(Files.newOutputStream(jar))) {
            for (var entry : entries.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey()));
                output.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
        return jar;
    }

    @Test void copiesNestedFilesWithoutDirectoryEntriesAndNeverOverwrites() throws Exception {
        Path source = jar(Map.of("Menus/main.yml", "name: Default", "Menus/Shops/blacksmith.yml", "name: Smith",
                "MenusExtra/ignored.yml", "ignored", "unrelated.yml", "ignored"));
        Path target = temp.resolve("data");
        assertEquals(2, JFilesManager.copyResourceFolder(source, target, "Menus"));
        assertEquals("name: Smith", Files.readString(target.resolve("Menus/Shops/blacksmith.yml")));
        Files.writeString(target.resolve("Menus/main.yml"), "name: Custom");
        assertEquals(0, JFilesManager.copyResourceFolder(source, target, "Menus/"));
        assertEquals("name: Custom", Files.readString(target.resolve("Menus/main.yml")));
        assertFalse(Files.exists(target.resolve("MenusExtra")));
        assertFalse(Files.exists(target.resolve("unrelated.yml")));
        source = jar(Map.of("Menus/main.yml", "updated default", "Menus/new.yml", "new"));
        assertEquals(1, JFilesManager.copyResourceFolder(source, target, "Menus"));
        assertEquals("name: Custom", Files.readString(target.resolve("Menus/main.yml")));
    }

    @Test void copiesBinaryFilesAndExplicitEmptyFolders() throws Exception {
        Path source = jar(Map.of("Assets/empty/", "", "Assets/nested/test.bin", "\u0000\u0001\u0002"));
        Path target = temp.resolve("data");
        assertEquals(1, JFilesManager.copyResourceFolder(source, target, "Assets"));
        assertTrue(Files.isDirectory(target.resolve("Assets/empty")));
        assertArrayEquals(new byte[]{0, 1, 2}, Files.readAllBytes(target.resolve("Assets/nested/test.bin")));
    }

    @Test void handlesExplodedResources() throws Exception {
        Path source = temp.resolve("classes");
        Files.createDirectories(source.resolve("Menus/nested"));
        Files.writeString(source.resolve("Menus/nested/test.yml"), "value: 1");
        assertEquals(1, JFilesManager.copyResourceFolder(source, temp.resolve("data"), "Menus"));
    }

    @Test void rejectsInvalidAndMissingFolderPaths() throws Exception {
        Path source = jar(Map.of("Menus/a.yml", "a"));
        for (String folder : new String[]{"", "/", "/Menus", "../Menus", "Menus/../Other", "C:/Menus", "Menus//Sub"}) {
            assertThrows(IllegalArgumentException.class, () -> JFilesManager.copyResourceFolder(source, temp.resolve("data"), folder));
        }
        assertThrows(IllegalArgumentException.class, () -> JFilesManager.copyResourceFolder(source, temp.resolve("data"), "Missing"));
    }

    @Test void rejectsTraversalInsideJar() throws Exception {
        Path source = jar(Map.of("Menus/../../outside.yml", "bad"));
        assertThrows(IOException.class, () -> JFilesManager.copyResourceFolder(source, temp.resolve("data"), "Menus"));
        assertFalse(Files.exists(temp.resolve("outside.yml")));
    }

    @Test void reportsFileDirectoryConflicts() throws Exception {
        Path source = jar(Map.of("Menus/a.yml", "a"));
        Path target = temp.resolve("data");
        Files.createDirectories(target.resolve("Menus/a.yml"));
        assertThrows(IOException.class, () -> JFilesManager.copyResourceFolder(source, target, "Menus"));
    }

    private Plugin plugin(Map<String, String> resources) {
        return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class}, (proxy, method, args) -> switch (method.getName()) {
            case "getDataFolder" -> temp.resolve("data").toFile();
            case "getResource" -> resources.containsKey(args[0]) ? new ByteArrayInputStream(resources.get(args[0]).getBytes(StandardCharsets.UTF_8)) : null;
            default -> throw new UnsupportedOperationException(method.getName());
        });
    }

    @Test void createYmlPrefersNestedDefault() {
        var manager = new JFilesManager<>(plugin(Map.of("Menus/main.yml", "value: nested", "main.yml", "value: flat")));
        assertEquals("nested", manager.createYML("main", "Menus").getConfig().getString("value"));
    }

    @Test void createYmlRetainsLegacyRootDefaultFallback() {
        var manager = new JFilesManager<>(plugin(Map.of("main.yml", "value: legacy")));
        assertEquals("legacy", manager.createYML("main", "Menus").getConfig().getString("value"));
    }

    @Test void createYmlPreservesExistingFile() throws Exception {
        Files.createDirectories(temp.resolve("data/Menus"));
        Files.writeString(temp.resolve("data/Menus/main.yml"), "value: custom");
        var manager = new JFilesManager<>(plugin(Map.of("Menus/main.yml", "value: default")));
        assertEquals("custom", manager.createYML("main", "Menus").getConfig().getString("value"));
    }
}
