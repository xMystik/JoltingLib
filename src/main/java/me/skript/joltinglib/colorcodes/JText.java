package me.skript.joltinglib.colorcodes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JText {


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
