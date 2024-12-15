package me.skript.joltinglib.colorcodes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JText {

    /**
     * Formats a string by converting custom hex color codes and alternate color codes
     * into valid Minecraft color codes.
     *
     * <p>Identifies hex color codes in the format {@code &#RRGGBB}, converts them
     * into Bukkit-supported color codes using the {@code &x&r&r&g&g&b&b} format, and processes
     * all color codes into valid {@link ChatColor}</p>
     *
     * @param message the string to format, which may include custom hex and alternate color codes
     * @return the formatted string with valid Minecraft color codes
     */
    public static String format(String message) {
        Pattern pattern = Pattern.compile("(&#[a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            // Entire matched pattern including &#
            String colorCode = matcher.group(1);
            // Extract just the hex code, excluding &#
            String hexCode = colorCode.substring(2);
            StringBuilder hexBuilder = new StringBuilder("&x");
            for (char c : hexCode.toCharArray()) {
                hexBuilder.append("&").append(c);
            }
            // Replace the entire matched pattern
            message = message.replace(colorCode, hexBuilder.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Sends a centered message to a player by calculating pixel width and adjusting the spacing.
     *
     * <p>This method measures the pixel width of the input message using a custom font info class
     * ({@link JFontInfo}) to account for character sizes and bold text. It then prepends spaces
     * to the message until it is visually centered in the Minecraft chat window (154 pixels wide).</p>
     *
     * <p>The method also formats the message using {@link #format(String)} before sending it.</p>
     *
     * @param player the player to whom the centered message will be sent
     * @param message the message to center and send; if null or empty, sends an empty line
     */
    public static void centerMessage(Player player, String message){
        if(message == null || message.equals("")) player.sendMessage("");
        message = format(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                JFontInfo dFI = JFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = JFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }

        player.sendMessage(sb + message);
    }

}
