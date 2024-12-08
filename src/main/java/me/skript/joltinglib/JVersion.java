package me.skript.joltinglib;

import org.bukkit.Bukkit;

public class JVersion {

    public static String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static boolean isVersion(String versionPrefix) {
        return getServerVersion().startsWith(versionPrefix);
    }

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
     * Takes as input the path of a class to check of its existence.
     *
     * @param classPath a package path followed by the class name
     * @return "True" if it exists, "False" if it does not.
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
