package me.skript.joltinglib.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class JText {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Formats a string by converting custom hex color codes and alternate color codes into valid Minecraft color codes using MiniMessage
     *
     * @param message the string to format, which may include custom hex and alternate color codes
     * @return the formatted string with valid color codes as a Component
     */
    public static Component format(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return miniMessage.deserialize(message).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Creates a styled button as a Component with hover and click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages.
     * The button text is formatted using MiniMessage for color codes and styling,
     * while hover event provides interactivity</p>
     *
     * @param buttonText the text to display on the button
     * @param hoverText the text to display when the button is hovered over
     * @return a Component representing the interactive button
     */
    public static Component createButton(String buttonText, String hoverText) {
        Component formattedButtonText = format(buttonText);
        Component formattedHoverText = format(hoverText);

        return Component.text()
                .append(formattedButtonText)
                .hoverEvent(HoverEvent.showText(formattedHoverText))
                .build();
    }

    /**
     * Creates a styled button as a Component with hover and click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages</p>
     *
     * @param buttonText the component to display on the button
     * @param hoverText the component to display when the button is hovered over
     * @return a Component representing the interactive button
     */
    public static Component createButton(Component buttonText, Component hoverText) {
        return Component.text()
                .append(buttonText)
                .hoverEvent(HoverEvent.showText(hoverText))
                .build();
    }

    /**
     * Creates a styled button as a Component with hover and click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages.
     * The button text is formatted using MiniMessage for color codes and styling,
     * while hover and click events provide interactivity</p>
     *
     * @param buttonText the text to display on the button
     * @param hoverText the text to display when the button is hovered over
     * @param clickAction the action to perform when the button is clicked
     * @param clickValue the value associated with the click action
     * @return a Component representing the interactive button
     */
    public static Component createButton(String buttonText, String hoverText, ClickEvent.Action clickAction, String clickValue) {
        Component formattedButtonText = format(buttonText);
        Component formattedHoverText = format(hoverText);

        return Component.text()
                .append(formattedButtonText)
                .hoverEvent(HoverEvent.showText(formattedHoverText))
                .clickEvent(ClickEvent.clickEvent(clickAction, clickValue))
                .build();
    }

    /**
     * Creates a styled button as a Component with hover and click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages</p>
     *
     * @param buttonComponent the component to display on the button
     * @param hoverComponent the component to display when the button is hovered over
     * @param clickAction the action to perform when the button is clicked
     * @param clickValue the value associated with the click action
     * @return a Component representing the interactive button
     */
    public static Component createButton(Component buttonComponent, Component hoverComponent, ClickEvent.Action clickAction, String clickValue) {
        return Component.text()
                .append(buttonComponent)
                .hoverEvent(HoverEvent.showText(hoverComponent))
                .clickEvent(ClickEvent.clickEvent(clickAction, clickValue))
                .build();
    }

    /**
     * Creates a styled button as a Component with click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages.
     * The button text is formatted using MiniMessage for color codes and styling,
     * while click events provide interactivity</p>
     *
     * @param buttonText the text to display on the button
     * @param clickAction the action to perform when the button is clicked
     * @param clickValue the value associated with the click action
     * @return a Component representing the interactive button
     */
    public static Component createButton(String buttonText, ClickEvent.Action clickAction, String clickValue) {
        Component formattedButtonText = format(buttonText);

        return Component.text()
                .append(formattedButtonText)
                .clickEvent(ClickEvent.clickEvent(clickAction, clickValue))
                .build();
    }

    /**
     * Creates a styled button as a Component with click functionality
     *
     * <p>This method allows for the creation of interactive buttons within chat messages.</p>
     *
     * @param buttonComponent the component to display on the button
     * @param clickAction the action to perform when the button is clicked
     * @param clickValue the value associated with the click action
     * @return a Component representing the interactive button
     */
    public static Component createButton(Component buttonComponent, ClickEvent.Action clickAction, String clickValue) {
        return Component.text()
                .append(buttonComponent)
                .clickEvent(ClickEvent.clickEvent(clickAction, clickValue))
                .build();
    }

    /**
     * Sends a message with placeholders replaced by specified Components to a player.
     *
     * <p>The placeholders are identified by curly braces, such as "{0}", "{1}", etc.,
     * and are replaced sequentially with the provided Components in the buttons array.</p>
     *
     * @param player the player that'll receive the message
     * @param message the message containing placeholders
     * @param buttons  the Components to replace the placeholders in order
     */
    public static void sendMessage(Player player, String message, Component... buttons) {
        if (message == null || message.isEmpty()) {
            player.sendMessage(Component.empty());
            return;
        }

        // Splits by placeholders like {0}, {1}, etc.
        String[] parts = message.split("\\{\\d+}");
        Component result = Component.empty();

        int buttonIndex = 0;

        for (String part : parts) {
            result = result.append(format(part));

            // Append the button Component only if there's a corresponding placeholder
            if (buttonIndex < buttons.length && message.contains("{" + buttonIndex + "}")) {
                result = result.append(buttons[buttonIndex++]);
            }
        }

        // If there are leftover buttons but no corresponding placeholders, they shouldn't be appended
        player.sendMessage(result);
    }

    /**
     * Sends a centered message to a player by calculating pixel width and adjusting the spacing
     *
     * <p>This method measures the pixel width of the input message using a custom font info class
     * ({@link JFontInfo}) to account for character sizes and bold text. It uses {@link MiniMessage}
     * for color codes and formatting</p>
     *
     * @param player the player to whom the centered message will be sent
     * @param message the message to center and send; if null or empty, sends an empty line
     */
    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }

        Component formattedComponent = format(message);
        String plainMessage = LegacyComponentSerializer.legacySection().serialize(formattedComponent);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : plainMessage.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
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

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        player.sendMessage(sb + plainMessage);
    }

    /**
     * Sends a centered message to a player by calculating pixel width and adjusting the spacing
     *
     * <p>This method measures the pixel width of the input message using a custom font info class
     * ({@link JFontInfo}) to account for character sizes and bold text. It uses {@link MiniMessage}
     * for color codes and formatting. The method also supports inserting buttons as placeholders
     * within the message, which are inserted at the corresponding positions in the message's layout</p>
     *
     * @param player the player to whom the centered message will be sent
     * @param message the message to center and send, which may contain placeholders such as "{0}", "{1}"
     * @param buttons the Components to replace the placeholders in the message, in order
     */
    public static void sendCenteredMessage(Player player, String message, Component... buttons) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }

        // Split the message by placeholders like {0}, {1}, etc.
        String[] parts = message.split("\\{\\d+}");
        Component result = Component.empty();

        int buttonIndex = 0;

        for (String part : parts) {
            result = result.append(format(part));

            // Append the replacement Component (button) only if there's a corresponding placeholder
            if (buttonIndex < buttons.length && message.contains("{" + buttonIndex + "}")) {
                result = result.append(buttons[buttonIndex++]);
            }
        }

        String plainMessage = LegacyComponentSerializer.legacySection().serialize(result);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : plainMessage.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else {
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

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        // Create a Component with the centered text (spaces + the original message with buttons)
        Component centeredMessage = Component.text(sb.toString()).append(result);
        player.sendMessage(centeredMessage);
    }

}
