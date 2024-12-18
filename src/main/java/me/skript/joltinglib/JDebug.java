package me.skript.joltinglib;

import java.util.logging.Level;

public class JDebug {

    public static void log(Level level, String message) {
        JoltingLib.getInstance().getLogger().log(level, "[JDebug] " + message);
    }

}
