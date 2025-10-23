package me.skript.joltinglib;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class JDebug {

    private JDebug() {}

    /**
     * Logs a message to the server console
     *
     * @param message the message to be logged
     */
    public static void log(String message) {
        if(JoltingLib.getInstance().getConfigurationFile().getBoolean("debug-enabled", false)) {
            JoltingLib.getInstance().getLogger().log(Level.INFO, message);
        }
    }

    /**
     * Logs a message with a specified log level to the server console
     *
     * @param level the logging level to indicate the severity of the message
     * @param message the message to be logged
     */
    public static void log(Level level, String message) {
        if(JoltingLib.getInstance().getConfigurationFile().getBoolean("debug-enabled", false)) {
            JoltingLib.getInstance().getLogger().log(level, message);
        }
    }

    /**
     * @return the current server version number
     */
    public static String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * Compares the exact server version with the specified version
     *
     * @param targetVersion the version to check for
     * @return true if it's the same, false otherwise
     */
    public static boolean isVersion(String targetVersion) {
        return getServerVersion().startsWith(targetVersion);
    }

    /**
     * Compares the current server version with the specified version
     *
     * @param targetVersion the version to check for
     * @return true if it's at least the compared version, false otherwise
     */
    public static boolean isVersionAtLeast(String targetVersion) {
        String[] current = getServerVersion().split("\\.");
        String[] target = targetVersion.split("\\.");

        for (int i = 0; i < target.length; i++) {
            int currentNum = (i < current.length) ? Integer.parseInt(current[i]) : 0;
            int targetNum = Integer.parseInt(target[i]);

            if (currentNum < targetNum) {
                return false;
            } else if (currentNum > targetNum) {
                return true;
            }
        }
        return true;
    }

    /**
     * Takes as input the path of a class to check of its existence
     *
     * @param classPath a package path followed by the class name
     * @return true if it exists, false otherwise
     */
    public static boolean isClassAvailable(String classPath) {
        try {
            Class.forName(classPath);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
