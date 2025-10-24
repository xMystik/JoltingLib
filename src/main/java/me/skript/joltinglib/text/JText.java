package me.skript.joltinglib.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;

public class JText {

    private JText () {}

    /**
     * Parses and formats a string supporting both <b>legacy color codes</b> (e.g. {@code &c}, {@code §6}, {@code &#ff0000})
     * and <b>MiniMessage tags</b> (e.g. {@code <bold>}, {@code <gradient:#00ffcc:#ff00ff>}).
     * <p>
     * This method allows mixed usage of both systems within the same input,
     * seamlessly converting legacy color codes into MiniMessage-compatible tags before
     * processing the entire string with {@link net.kyori.adventure.text.minimessage.MiniMessage}.
     * <p>
     * For example:
     * <pre>{@code
     * JText.format("&6<bold>Hello &cWorld!</bold>");
     * }</pre>
     * Produces a gold bold "Hello" followed by red bold "World!".
     * <p>
     * <b>Supported features:</b>
     * <ul>
     *     <li>Legacy color codes using {@code &} or {@code §} prefixes (0–9, a–f)</li>
     *     <li>Legacy formatting codes: {@code &l} (bold), {@code &n} (underline), {@code &o} (italic), {@code &m} (strikethrough), {@code &k} (obfuscated), {@code &r} (reset)</li>
     *     <li>Legacy hex colors in the form {@code &#RRGGBB}</li>
     *     <li>Full MiniMessage support for tags like {@code <gradient>}, {@code <hover>}, {@code <click>}, etc.</li>
     *     <li>Automatic normalization of § to &</li>
     *     <li>Automatic fallback to legacy deserialization if MiniMessage parsing fails</li>
     * </ul>
     *
     * @param message the text to format; may include legacy and/or MiniMessage formatting
     * @return a fully formatted {@link net.kyori.adventure.text.Component}
     */
    public static Component format(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        // Normalize section char to ampersand
        String s = message.replace('§', '&');

        StringBuilder out = new StringBuilder(s.length() * 2);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '&' && i + 1 < s.length()) {
                char code = Character.toLowerCase(s.charAt(i + 1));

                // Hex form: &#[RRGGBB]
                if (code == '#' && i + 7 < s.length()) {
                    String maybeHex = s.substring(i + 2, i + 8);
                    if (maybeHex.matches("(?i)[0-9a-f]{6}")) {
                        out.append("<#").append(maybeHex).append(">");
                        i += 7;
                        continue;
                    }
                }

                // Map legacy code to MiniMessage tag(s)
                String tag = switch (code) {
                    // Colors
                    case '0' -> "<black>";
                    case '1' -> "<dark_blue>";
                    case '2' -> "<dark_green>";
                    case '3' -> "<dark_aqua>";
                    case '4' -> "<dark_red>";
                    case '5' -> "<dark_purple>";
                    case '6' -> "<gold>";
                    case '7' -> "<gray>";
                    case '8' -> "<dark_gray>";
                    case '9' -> "<blue>";
                    case 'a' -> "<green>";
                    case 'b' -> "<aqua>";
                    case 'c' -> "<red>";
                    case 'd' -> "<light_purple>";
                    case 'e' -> "<yellow>";
                    case 'f' -> "<white>";

                    // Formats
                    case 'k' -> "<obfuscated>";
                    case 'l' -> "<bold>";
                    case 'm' -> "<strikethrough>";
                    case 'n' -> "<underline>";
                    case 'o' -> "<italic>";

                    // Reset
                    case 'r' -> "<reset>";

                    default -> null;
                };

                if (tag != null) {
                    out.append(tag);
                    i++; // skip the code character
                    continue;
                }

                // Not a recognized legacy token -> append literal '&'
                out.append('&');
                continue;
            }

            // Normal char
            out.append(c);
        }

        String combined = out.toString();

        // Parse with MiniMessage
        try {
            return MiniMessage.miniMessage().deserialize(combined).decoration(TextDecoration.ITALIC, false);
        } catch (Exception ex) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message).decoration(TextDecoration.ITALIC, false);
        }
    }

    /**
     * Converts a message containing MiniMessage or legacy color codes into a
     * legacy-formatted string using the '§' section symbol.
     * <p>
     * This method first processes MiniMessage-style formatting (via {@link JText#format(String)}),
     * then serializes the resulting component back into legacy text.
     *
     * @param message The input message containing MiniMessage or legacy formatting codes.
     * @return The fully formatted legacy string, ready for display in Minecraft.
     */
    public static String formatLegacy(String message) {
        return LegacyComponentSerializer.legacySection().serialize(JText.format(message));
    }

    /**
     * Formats a component to plain text
     *
     * @param component the component to format into plain text
     * @return the formatted string
     */
    public static String plainTextFormat(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Replaces placeholders in a given Component with either another Component or a string.
     *
     * <p>This method scans a Component's content for a placeholder string and replaces it with the
     * specified replacement. Both Components and Strings can be replacements.</p>
     *
     * @param component the base Component containing placeholder strings
     * @param placeholder the placeholder string to replace (e.g., "{placeholder}")
     * @param replacement the replacement, which can be either a Component or a string
     * @return a new Component with the placeholder replaced
     */
    public static Component replace(Component component, String placeholder, Object replacement) {
        if (component == null || placeholder == null || replacement == null) {
            return component; // Return the original Component if any argument is null
        }

        // Convert the original component to plain text for easier manipulation
        String rawText = PlainTextComponentSerializer.plainText().serialize(component);

        // Check if the placeholder exists
        if (!rawText.contains(placeholder)) {
            return component; // If the placeholder is not present, return the original component
        }

        // Determine what type of replacement to use (Component or String)
        Component replacementComponent = (replacement instanceof Component)
                ? (Component) replacement
                : Component.text(replacement.toString());

        String[] parts = rawText.split(placeholder, -1);

        // Rebuild the final component with the replacement included
        Component result = Component.empty();

        for (int i = 0; i < parts.length; i++) {
            // Append the current text part
            result = result.append(Component.text(parts[i]));

            // Append the replacement only if it's not the last part
            if (i < parts.length - 1) {
                result = result.append(replacementComponent);
            }
        }

        return result;
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
     * and are replaced sequentially with the provided Components in the button array.</p>
     *
     * @param player the player that'll receive the message
     * @param message the message containing placeholders
     * @param buttons the Components to replace the placeholders in order
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

    /**
     * Sends a centered message to a player by calculating pixel width and adjusting the spacing.
     *
     * <p>This method measures the pixel width of the input Component's plain text using a custom font info class
     * ({@link JFontInfo}) to account for character sizes and bold text. It uses {@link MiniMessage}
     * for color codes and formatting.</p>
     *
     * @param player the player to whom the centered message will be sent
     * @param component the Component to center and send; if null, sends an empty line
     */
    public static void sendCenteredMessage(Player player, Component component) {
        if (component == null || component.equals(Component.empty())) {
            player.sendMessage(Component.empty());
            return;
        }

        String plainMessage = PlainTextComponentSerializer.plainText().serialize(component);

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

        // Send the centered message
        Component centeredMessage = Component.text(sb.toString()).append(component);
        player.sendMessage(centeredMessage);
    }

    /**
     * Sends a centered message to a player by calculating pixel width and adjusting the spacing.
     *
     * <p>This method measures the pixel width of the input Component's plain text using a custom font info class
     * ({@link JFontInfo}) to account for character sizes and bold text. It uses {@link MiniMessage}
     * for color codes and formatting. The method also supports inserting buttons as placeholders
     * within the Component, which are inserted at the corresponding positions in the layout</p>
     *
     * @param player the player to whom the centered message will be sent
     * @param component the Component to center and send, which may contain placeholders
     * @param buttons the Components to replace the placeholders, in order
     */
    public static void sendCenteredMessage(Player player, Component component, Component... buttons) {
        if (component == null || component.equals(Component.empty())) {
            player.sendMessage(Component.empty());
            return;
        }

        // Convert the Component to plain text for measuring pixel size
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(component);

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

        // Replace placeholders with buttons
        Component formattedComponent = component;
        for (int i = 0; i < buttons.length; i++) {
            formattedComponent = replace(formattedComponent, "{" + i + "}", buttons[i]);
        }

        // Create and send the centered message
        Component centeredMessage = Component.text(sb.toString()).append(formattedComponent);
        player.sendMessage(centeredMessage);
    }

    /**
     * Sends centered messages to a player from a list of either Strings or Components by calculating pixel width and adjusting spacing.
     *
     * <p>If the list contains Strings, each string gets formatted into a Component using {@link MiniMessage}.
     * If the list contains Components, each component is sent as it is after centering it properly.</p>
     *
     * @param player the player to whom the messages will be sent
     * @param messages the list of messages, either Strings or Components; if null or empty, sends nothing
     */
    public static void sendCenteredMessages(Player player, List<?> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (Object message : messages) {
            if (message instanceof String) {
                sendCenteredMessage(player, (String) message);
            } else if (message instanceof Component) {
                sendCenteredMessage(player, (Component) message);
            } else {
                throw new IllegalArgumentException("List contains unsupported type. Only Strings or Components are allowed.");
            }
        }
    }

    /**
     * Sends centered messages to a player from a list of either Strings or Components with buttons (placeholders).
     *
     * <p>This method automatically handles lists of Strings or Components. If the list contains Strings,
     * placeholders like "{0}" are replaced with the provided buttons before centering the message.
     * If the list contains Components, placeholders in each Component are replaced correspondingly.</p>
     *
     * @param player the player to whom the messages will be sent
     * @param messages the list of messages, either Strings or Components, containing placeholders; if null or empty, sends nothing
     * @param buttons the Components to replace the placeholders (e.g., "{0}", "{1}"), in order
     */
    public static void sendCenteredMessages(Player player, List<?> messages, Component... buttons) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (Object message : messages) {
            if (message instanceof String) {
                sendCenteredMessage(player, (String) message, buttons);
            } else if (message instanceof Component) {
                sendCenteredMessage(player, (Component) message, buttons);
            } else {
                throw new IllegalArgumentException("List contains unsupported type. Only Strings or Components are allowed.");
            }
        }
    }

}
