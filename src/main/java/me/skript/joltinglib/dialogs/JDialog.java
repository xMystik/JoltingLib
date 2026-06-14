package me.skript.joltinglib.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import me.skript.joltinglib.text.JText;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JDialog {

    private Component title;
    private Component externalTitle;
    private boolean canCloseWithEscape = true;
    private boolean pause = false;
    private DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.CLOSE;

    private final List<DialogBody> bodies = new ArrayList<>();
    private final List<DialogInput> inputs = new ArrayList<>();
    private final List<ActionButton> actions = new ArrayList<>();

    private DialogType type;
    private ActionButton noticeAction;
    private ActionButton yesButton;
    private ActionButton noButton;
    private ActionButton exitAction;
    private int columns = 1;
    private int buttonWidth = 150;

    /**
     * Creates a new dialog builder.
     *
     * @param title the title displayed at the top of the dialog
     */
    public JDialog(String title) {
        this(JText.format(title));
    }

    /**
     * Creates a new dialog builder.
     *
     * @param title the title displayed at the top of the dialog
     */
    public JDialog(Component title) {
        this.title = title;
    }

    /**
     * Creates a new dialog builder.
     *
     * @param title the title displayed at the top of the dialog
     */
    public static JDialog create(String title) {
        return new JDialog(title);
    }

    /**
     * Creates a new dialog builder.
     *
     * @param title the title displayed at the top of the dialog
     */
    public static JDialog create(Component title) {
        return new JDialog(title);
    }

    /**
     * Creates a notice dialog with a plain message body.
     *
     * @param title the title displayed at the top of the dialog
     * @param message the message displayed inside the dialog
     */
    public static JDialog notice(String title, String message) {
        return create(title).message(message).notice();
    }

    /**
     * Creates a confirmation dialog with yes and no buttons.
     *
     * @param title the title displayed at the top of the dialog
     * @param yesButton the confirmation button
     * @param noButton the denial button
     */
    public static JDialog confirmation(String title, ActionButton yesButton, ActionButton noButton) {
        return create(title).confirmation(yesButton, noButton);
    }

    /**
     * Sets the title displayed at the top of the dialog.
     *
     * @param title the title component
     */
    public JDialog setTitle(Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the title displayed at the top of the dialog.
     *
     * @param title the title text
     */
    public JDialog setTitle(String title) {
        return setTitle(JText.format(title));
    }

    /**
     * Sets the title used by buttons that open this dialog.
     *
     * @param externalTitle the external title component
     */
    public JDialog setExternalTitle(Component externalTitle) {
        this.externalTitle = externalTitle;
        return this;
    }

    /**
     * Sets the title used by buttons that open this dialog.
     *
     * @param externalTitle the external title text
     */
    public JDialog setExternalTitle(String externalTitle) {
        return setExternalTitle(JText.format(externalTitle));
    }

    /**
     * Sets whether the escape key can close this dialog.
     *
     * @param canCloseWithEscape true if escape can close the dialog
     */
    public JDialog setCanCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
        return this;
    }

    /**
     * Sets whether this dialog pauses the game in single-player.
     *
     * @param pause true if the dialog should pause single-player
     */
    public JDialog setPause(boolean pause) {
        this.pause = pause;
        return this;
    }

    /**
     * Sets what the client should do after a dialog action closes.
     *
     * @param afterAction the action to use after closing
     */
    public JDialog setAfterAction(DialogBase.DialogAfterAction afterAction) {
        this.afterAction = afterAction;
        return this;
    }

    /**
     * Adds a plain text body to the dialog.
     *
     * @param message the message text
     */
    public JDialog message(String message) {
        return message(JText.format(message));
    }

    /**
     * Adds a plain text body to the dialog.
     *
     * @param message the message component
     */
    public JDialog message(Component message) {
        this.bodies.add(DialogBody.plainMessage(message));
        return this;
    }

    /**
     * Adds a plain text body to the dialog with a specific width.
     *
     * @param message the message text
     * @param width the body width, between 1 and 1024
     */
    public JDialog message(String message, int width) {
        return message(JText.format(message), width);
    }

    /**
     * Adds a plain text body to the dialog with a specific width.
     *
     * @param message the message component
     * @param width the body width, between 1 and 1024
     */
    public JDialog message(Component message, int width) {
        this.bodies.add(DialogBody.plainMessage(message, clamp(width, 1, 1024)));
        return this;
    }

    /**
     * Adds an item body to the dialog.
     *
     * @param item the item to display
     */
    public JDialog item(ItemStack item) {
        this.bodies.add(DialogBody.item(item).build());
        return this;
    }

    /**
     * Adds an item body to the dialog and exposes Paper's item body builder.
     *
     * @param item the item to display
     * @param customizer the builder customizer
     */
    public JDialog item(ItemStack item, Consumer<io.papermc.paper.registry.data.dialog.body.ItemDialogBody.Builder> customizer) {
        io.papermc.paper.registry.data.dialog.body.ItemDialogBody.Builder builder = DialogBody.item(item);
        customizer.accept(builder);
        this.bodies.add(builder.build());
        return this;
    }

    /**
     * Adds an item body to the dialog.
     *
     * @param item the item to display
     * @param description the optional description below the item
     * @param showDecorations true to show item decorations
     * @param showTooltip true to show the item tooltip
     * @param width the item body width, between 1 and 256
     * @param height the item body height, between 1 and 256
     */
    public JDialog item(ItemStack item, String description, boolean showDecorations, boolean showTooltip, int width, int height) {
        PlainMessageDialogBody body = description == null ? null : DialogBody.plainMessage(JText.format(description));
        this.bodies.add(DialogBody.item(item, body, showDecorations, showTooltip, clamp(width, 1, 256), clamp(height, 1, 256)));
        return this;
    }

    /**
     * Adds an existing Paper dialog body.
     *
     * @param body the body to add
     */
    public JDialog addBody(DialogBody body) {
        this.bodies.add(body);
        return this;
    }

    /**
     * Clears all body elements.
     */
    public JDialog clearBody() {
        this.bodies.clear();
        return this;
    }

    /**
     * Adds a text input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     */
    public JDialog textInput(String key, String label) {
        this.inputs.add(DialogInput.text(key, JText.format(label)).build());
        return this;
    }

    /**
     * Adds a text input to the dialog and exposes Paper's text input builder.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param customizer the builder customizer
     */
    public JDialog textInput(String key, String label, Consumer<TextDialogInput.Builder> customizer) {
        TextDialogInput.Builder builder = DialogInput.text(key, JText.format(label));
        customizer.accept(builder);
        this.inputs.add(builder.build());
        return this;
    }

    /**
     * Adds a text input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param initial the initial text
     * @param maxLength the maximum text length
     */
    public JDialog textInput(String key, String label, String initial, int maxLength) {
        this.inputs.add(DialogInput.text(key, JText.format(label))
                .initial(initial)
                .maxLength(maxLength)
                .build());
        return this;
    }

    /**
     * Adds a multiline text input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param initial the initial text
     * @param maxLength the maximum text length
     * @param maxLines the maximum line count, or null
     * @param height the input height, or null
     */
    public JDialog multilineTextInput(String key, String label, String initial, int maxLength, Integer maxLines, Integer height) {
        this.inputs.add(DialogInput.text(key, JText.format(label))
                .initial(initial)
                .maxLength(maxLength)
                .multiline(TextDialogInput.MultilineOptions.create(maxLines, height))
                .build());
        return this;
    }

    /**
     * Adds a boolean input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param initial the initial value
     */
    public JDialog booleanInput(String key, String label, boolean initial) {
        this.inputs.add(DialogInput.bool(key, JText.format(label))
                .initial(initial)
                .build());
        return this;
    }

    /**
     * Adds a boolean input to the dialog and exposes Paper's boolean input builder.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param customizer the builder customizer
     */
    public JDialog booleanInput(String key, String label, Consumer<io.papermc.paper.registry.data.dialog.input.BooleanDialogInput.Builder> customizer) {
        io.papermc.paper.registry.data.dialog.input.BooleanDialogInput.Builder builder = DialogInput.bool(key, JText.format(label));
        customizer.accept(builder);
        this.inputs.add(builder.build());
        return this;
    }

    /**
     * Adds a boolean input to the dialog with command-template values.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param initial the initial value
     * @param onTrue the template value when true
     * @param onFalse the template value when false
     */
    public JDialog booleanInput(String key, String label, boolean initial, String onTrue, String onFalse) {
        this.inputs.add(DialogInput.bool(key, JText.format(label), initial, onTrue, onFalse));
        return this;
    }

    /**
     * Adds a number range input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param start the minimum value
     * @param end the maximum value
     */
    public JDialog numberRangeInput(String key, String label, float start, float end) {
        this.inputs.add(DialogInput.numberRange(key, JText.format(label), start, end).build());
        return this;
    }

    /**
     * Adds a number range input to the dialog and exposes Paper's number range builder.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param start the minimum value
     * @param end the maximum value
     * @param customizer the builder customizer
     */
    public JDialog numberRangeInput(String key, String label, float start, float end, Consumer<io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput.Builder> customizer) {
        io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput.Builder builder = DialogInput.numberRange(key, JText.format(label), start, end);
        customizer.accept(builder);
        this.inputs.add(builder.build());
        return this;
    }

    /**
     * Adds a number range input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param start the minimum value
     * @param end the maximum value
     * @param initial the initial value, or null
     * @param step the step size, or null
     */
    public JDialog numberRangeInput(String key, String label, float start, float end, Float initial, Float step) {
        this.inputs.add(DialogInput.numberRange(key, JText.format(label), start, end)
                .initial(initial)
                .step(step)
                .build());
        return this;
    }

    /**
     * Adds a single-option input to the dialog.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param entries the available options
     */
    public JDialog singleOptionInput(String key, String label, List<SingleOptionDialogInput.OptionEntry> entries) {
        this.inputs.add(DialogInput.singleOption(key, JText.format(label), entries).build());
        return this;
    }

    /**
     * Adds a single-option input to the dialog and exposes Paper's single-option builder.
     *
     * @param key the input key used to read the response
     * @param label the input label
     * @param entries the available options
     * @param customizer the builder customizer
     */
    public JDialog singleOptionInput(String key, String label, List<SingleOptionDialogInput.OptionEntry> entries, Consumer<SingleOptionDialogInput.Builder> customizer) {
        SingleOptionDialogInput.Builder builder = DialogInput.singleOption(key, JText.format(label), entries);
        customizer.accept(builder);
        this.inputs.add(builder.build());
        return this;
    }

    /**
     * Adds an existing Paper dialog input.
     *
     * @param input the input to add
     */
    public JDialog addInput(DialogInput input) {
        this.inputs.add(input);
        return this;
    }

    /**
     * Clears all inputs.
     */
    public JDialog clearInputs() {
        this.inputs.clear();
        return this;
    }

    /**
     * Sets this dialog to notice mode with the default action.
     */
    public JDialog notice() {
        this.type = DialogType.notice();
        return this;
    }

    /**
     * Sets this dialog to notice mode with a custom action button.
     *
     * @param action the notice action button
     */
    public JDialog notice(ActionButton action) {
        this.noticeAction = action;
        this.type = DialogType.notice(action);
        return this;
    }

    /**
     * Sets this dialog to confirmation mode.
     *
     * @param yesButton the confirmation button
     * @param noButton the denial button
     */
    public JDialog confirmation(ActionButton yesButton, ActionButton noButton) {
        this.yesButton = yesButton;
        this.noButton = noButton;
        this.type = DialogType.confirmation(yesButton, noButton);
        return this;
    }

    /**
     * Sets this dialog to confirmation mode with response callbacks.
     *
     * @param yesLabel the confirmation button label
     * @param noLabel the denial button label
     * @param onYes the callback for the confirmation button
     * @param onNo the callback for the denial button
     */
    public JDialog confirm(String yesLabel, String noLabel, ResponseCallback onYes, ResponseCallback onNo) {
        return confirmation(
                button(yesLabel, responseAction(onYes)),
                button(noLabel, responseAction(onNo))
        );
    }

    /**
     * Sets this dialog to notice mode with a response callback submit button.
     *
     * @param label the submit button label
     * @param callback the callback to run
     */
    public JDialog submit(String label, ResponseCallback callback) {
        return notice(button(label, responseAction(callback)));
    }

    /**
     * Sets this dialog to notice mode with a simple close button.
     *
     * @param label the close button label
     */
    public JDialog closeButton(String label) {
        return notice(button(label));
    }

    /**
     * Sets this dialog to notice mode with a player callback button.
     *
     * @param label the button label
     * @param callback the callback to run
     */
    public JDialog backButton(String label, Consumer<Player> callback) {
        return notice(button(label, responseAction((response, audience) -> callback.accept(requirePlayer(audience)))));
    }

    /**
     * Sets this dialog to multi-action mode.
     */
    public JDialog multiAction() {
        this.type = null;
        return this;
    }

    /**
     * Sets this dialog to server-links mode.
     *
     * @param columns the number of columns
     * @param buttonWidth the width of each link button
     */
    public JDialog serverLinks(int columns, int buttonWidth) {
        this.columns = clamp(columns, 1, 64);
        this.buttonWidth = clamp(buttonWidth, 1, 1024);
        this.type = DialogType.serverLinks(exitAction, this.columns, this.buttonWidth);
        return this;
    }

    /**
     * Sets this dialog to dialog-list mode.
     *
     * @param dialogs the dialogs to list
     * @param columns the number of columns
     * @param buttonWidth the width of each dialog button
     */
    public JDialog dialogList(RegistrySet<Dialog> dialogs, int columns, int buttonWidth) {
        this.columns = clamp(columns, 1, 64);
        this.buttonWidth = clamp(buttonWidth, 1, 1024);
        this.type = DialogType.dialogList(dialogs, exitAction, this.columns, this.buttonWidth);
        return this;
    }

    /**
     * Sets the raw Paper dialog type. Use this for API features not covered by convenience methods.
     *
     * @param type the dialog type
     */
    public JDialog setType(DialogType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds an action button to a multi-action dialog.
     *
     * @param button the button to add
     */
    public JDialog addButton(ActionButton button) {
        this.actions.add(button);
        return this;
    }

    /**
     * Adds an action button to a multi-action dialog.
     *
     * @param label the button label
     * @param action the action to run
     */
    public JDialog addButton(String label, DialogAction action) {
        return addButton(button(label, action));
    }

    /**
     * Adds an action button to a multi-action dialog and exposes Paper's action button builder.
     *
     * @param label the button label
     * @param customizer the builder customizer
     */
    public JDialog addButton(String label, Consumer<ActionButton.Builder> customizer) {
        return addButton(button(label, customizer));
    }

    /**
     * Adds a command button to a multi-action dialog.
     *
     * @param label the button label
     * @param command the command to run
     */
    public JDialog addCommandButton(String label, String command) {
        return addButton(label, command(command));
    }

    /**
     * Adds a command-template button to a multi-action dialog.
     * <p>
     * Paper command templates are macro commands and must include at least one input variable.
     * Use {@link #addCommandButton(String, String)} for normal fixed commands.
     *
     * @param label the button label
     * @param commandTemplate the command template to run
     */
    public JDialog addCommandTemplateButton(String label, String commandTemplate) {
        return addButton(label, commandTemplate(commandTemplate));
    }

    /**
     * Adds a callback button to a multi-action dialog.
     *
     * @param label the button label
     * @param callback the callback to run
     * @param options callback options
     */
    public JDialog addCallbackButton(String label, DialogActionCallback callback, ClickCallback.Options options) {
        return addButton(label, DialogAction.customClick(callback, options));
    }

    /**
     * Adds a callback button to a multi-action dialog.
     *
     * @param label the button label
     * @param callback the callback to run
     * @param options callback options
     */
    public JDialog addCallbackButton(String label, ResponseCallback callback, ClickCallback.Options options) {
        return addButton(label, callback(callback, options));
    }

    /**
     * Adds a response callback button to a multi-action dialog.
     *
     * @param label the button label
     * @param callback the callback to run
     */
    public JDialog addResponseButton(String label, ResponseCallback callback) {
        return addResponseButton(label, callback, unlimitedOptions());
    }

    /**
     * Adds a response callback button to a multi-action dialog.
     *
     * @param label the button label
     * @param callback the callback to run
     * @param options callback options
     */
    public JDialog addResponseButton(String label, ResponseCallback callback, ClickCallback.Options options) {
        return addButton(label, responseAction(callback, options));
    }

    /**
     * Sets the button shown for exiting multi-action, dialog-list, or server-links dialogs.
     *
     * @param exitAction the exit action button
     */
    public JDialog setExitAction(ActionButton exitAction) {
        this.exitAction = exitAction;
        return this;
    }

    /**
     * Sets the number of columns used by multi-action dialogs.
     *
     * @param columns the number of columns
     */
    public JDialog setColumns(int columns) {
        this.columns = clamp(columns, 1, 64);
        return this;
    }

    /**
     * Sets the width used by dialog-list and server-links buttons.
     *
     * @param buttonWidth the button width
     */
    public JDialog setButtonWidth(int buttonWidth) {
        this.buttonWidth = clamp(buttonWidth, 1, 1024);
        return this;
    }

    /**
     * Builds the Paper dialog.
     */
    public Dialog build() {
        validate();

        DialogBase base = DialogBase.builder(title)
                .externalTitle(externalTitle)
                .canCloseWithEscape(canCloseWithEscape)
                .pause(pause)
                .afterAction(afterAction)
                .body(bodies)
                .inputs(inputs)
                .build();

        DialogType finalType = type != null ? type : buildImplicitType();
        return Dialog.create(factory -> factory.empty().base(base).type(finalType));
    }

    /**
     * Shows the dialog to an audience.
     *
     * @param audience the audience that receives the dialog
     */
    public JDialog show(Audience audience) {
        audience.showDialog(build());
        return this;
    }

    /**
     * Shows the dialog to a player.
     *
     * @param player the player that receives the dialog
     */
    public JDialog show(Player player) {
        return show((Audience) player);
    }

    private DialogType buildImplicitType() {
        if (yesButton != null && noButton != null) {
            return DialogType.confirmation(yesButton, noButton);
        }

        if (!actions.isEmpty()) {
            return DialogType.multiAction(actions, exitAction, columns);
        }

        return noticeAction == null ? DialogType.notice() : DialogType.notice(noticeAction);
    }

    /**
     * Creates a simple action button.
     *
     * @param label the button label
     */
    public static ActionButton button(String label) {
        return ActionButton.builder(JText.format(label)).build();
    }

    /**
     * Creates an action button.
     *
     * @param label the button label
     * @param action the action to run when clicked
     */
    public static ActionButton button(String label, DialogAction action) {
        return ActionButton.builder(JText.format(label)).action(action).build();
    }

    /**
     * Creates an action button and exposes Paper's action button builder.
     *
     * @param label the button label
     * @param customizer the builder customizer
     */
    public static ActionButton button(String label, Consumer<ActionButton.Builder> customizer) {
        ActionButton.Builder builder = ActionButton.builder(JText.format(label));
        customizer.accept(builder);
        return builder.build();
    }

    /**
     * Creates an action button.
     *
     * @param label the button label
     * @param tooltip the tooltip shown on hover
     * @param width the button width, between 1 and 1024
     * @param action the action to run when clicked
     */
    public static ActionButton button(String label, String tooltip, int width, DialogAction action) {
        return ActionButton.builder(JText.format(label))
                .tooltip(tooltip == null ? null : JText.format(tooltip))
                .width(clamp(width, 1, 1024))
                .action(action)
                .build();
    }

    /**
     * Creates a normal command action.
     *
     * @param command the command to run
     */
    public static DialogAction command(String command) {
        return DialogAction.staticAction(ClickEvent.runCommand(normalizeCommand(command)));
    }

    /**
     * Creates a command-template action.
     * <p>
     * Paper command templates are macro commands and must include at least one input variable.
     *
     * @param commandTemplate the command template to run
     */
    public static DialogAction commandTemplate(String commandTemplate) {
        if (commandTemplate == null || !commandTemplate.contains("$(")) {
            throw new IllegalArgumentException("Command templates must contain at least one input variable, for example $(name). Use command(...) for fixed commands.");
        }
        return DialogAction.commandTemplate(commandTemplate);
    }

    /**
     * Creates a static click-event action.
     *
     * @param clickEvent the click event to run
     */
    public static DialogAction click(ClickEvent clickEvent) {
        return DialogAction.staticAction(clickEvent);
    }

    /**
     * Creates a custom click action using a registered key.
     *
     * @param id the custom action id
     * @param additions additional payload data, or null
     */
    public static DialogAction custom(Key id, BinaryTagHolder additions) {
        return DialogAction.customClick(id, additions);
    }

    /**
     * Creates a custom callback action.
     *
     * @param callback the callback to run
     * @param options callback options
     */
    public static DialogAction callback(DialogActionCallback callback, ClickCallback.Options options) {
        return DialogAction.customClick(callback, options);
    }

    /**
     * Creates a custom callback action with a {@link Response} wrapper.
     *
     * @param callback the callback to run
     * @param options callback options
     */
    public static DialogAction callback(ResponseCallback callback, ClickCallback.Options options) {
        return responseAction(callback, options);
    }

    /**
     * Creates a custom callback action with a {@link Response} wrapper.
     *
     * @param callback the callback to run
     */
    public static DialogAction responseAction(ResponseCallback callback) {
        return responseAction(callback, unlimitedOptions());
    }

    /**
     * Creates a custom callback action with a {@link Response} wrapper.
     *
     * @param callback the callback to run
     * @param options callback options
     */
    public static DialogAction responseAction(ResponseCallback callback, ClickCallback.Options options) {
        return DialogAction.customClick((response, audience) -> callback.accept(new Response(response), audience), options);
    }

    /**
     * Creates callback options with unlimited uses and a ten minute lifetime.
     */
    public static ClickCallback.Options options() {
        return unlimitedOptions();
    }

    /**
     * Creates callback options with unlimited uses and the given lifetime.
     *
     * @param lifetime how long the callback remains usable
     */
    public static ClickCallback.Options options(Duration lifetime) {
        return options(ClickCallback.UNLIMITED_USES, lifetime);
    }

    /**
     * Creates one-use callback options with a ten minute lifetime.
     */
    public static ClickCallback.Options oneUseOptions() {
        return options(1, Duration.ofMinutes(10));
    }

    /**
     * Creates unlimited-use callback options with a ten minute lifetime.
     */
    public static ClickCallback.Options unlimitedOptions() {
        return options(ClickCallback.UNLIMITED_USES, Duration.ofMinutes(10));
    }

    /**
     * Creates callback options.
     *
     * @param uses how many times the callback can be used
     * @param lifetime how long the callback remains usable
     */
    public static ClickCallback.Options options(int uses, Duration lifetime) {
        return ClickCallback.Options.builder()
                .uses(uses)
                .lifetime(lifetime)
                .build();
    }

    private static String normalizeCommand(String command) {
        if (command == null || command.isBlank()) return "/";
        return command.startsWith("/") ? command : "/" + command;
    }

    private static Player requirePlayer(Audience audience) {
        if (audience instanceof Player player) return player;
        throw new IllegalStateException("This dialog action can only be used by players.");
    }

    private void validate() {
        if (title == null) {
            throw new IllegalStateException("Dialog title cannot be null.");
        }

        columns = clamp(columns, 1, 64);
        buttonWidth = clamp(buttonWidth, 1, 1024);

        if (!inputs.isEmpty() && actions.isEmpty() && noticeAction == null && yesButton == null && noButton == null && type == null) {
            throw new IllegalStateException("Dialogs with inputs need an action button to submit the response.");
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Creates a single-option entry.
     *
     * @param id the option id returned in responses
     * @param display the option display text
     * @param initial true if this option should be selected initially
     */
    public static SingleOptionDialogInput.OptionEntry option(String id, String display, boolean initial) {
        return SingleOptionDialogInput.OptionEntry.create(id, display == null ? null : JText.format(display), initial);
    }

    /**
     * Wraps a Paper dialog response with convenience getters.
     *
     * @param response the raw Paper response
     */
    public record Response(DialogResponseView response) {

        public String getText(String key) {
            return response.getText(key);
        }

        public Boolean getBoolean(String key) {
            return response.getBoolean(key);
        }

        public Float getFloat(String key) {
            return response.getFloat(key);
        }

        public BinaryTagHolder payload() {
            return response.payload();
        }
    }

    @FunctionalInterface
    public interface ResponseCallback {

        void accept(Response response, Audience audience);
    }
}
