package me.skript.joltinglib.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JMenu implements InventoryHolder {

    protected UUID owner;
    protected Inventory inventory;
    protected String title;
    protected int size;

    /**
     * Creates a new menu for the given player (owner). Initializes the menu's size, title,
     * and contents, then opens it for the player
     *
     * @param player the player to whom the menu will be opened
     */
    public JMenu(Player player) {
        owner = player.getUniqueId();
    }

    /**
     * Retrieves the size of the inventory
     *
     * @return the size of the inventory
     */
    public abstract int getSize();

    /**
     * Retrieves the title of the inventory
     *
     * @return the title of the inventory
     */
    public abstract String getTitle();

    /**
     * Sets up the contents of the menu. This method must be implemented
     * in the subclass to define the items and actions within the menu
     */
    public abstract void setupContents();

    /**
     * Handles the click events for the menu. This method must be implemented
     * in the subclass to define the actions triggered by player clicks in the menu
     *
     * @param event the click event triggered by a player in the menu
     */
    public abstract void handleClicks(InventoryClickEvent event);

    /**
     * Opens the menu for the owner of the menu
     * <p>
     * The menu size is validated to ensure it is between 9 and 54 slots,
     * and a default title is applied if no title is set. The contents are set
     * up using {@link #setupContents()} and the menu is then opened for the player
     */
    public void openMenu() {
        this.size = (getSize() < 9 || getSize() > 54) ? 54 : getSize();
        this.title = getTitle() != null ? getTitle() : "Undefined";

        inventory = Bukkit.createInventory(this, this.size, this.title);
        setupContents();

        Player player = Bukkit.getPlayer(owner);

        if (player != null && player.isOnline()) {
            player.openInventory(inventory);
        }
    }

    /**
     * Opens the menu for a specified player
     * <p>
     * Similar to {@link #openMenu()}, but opens the menu for the provided player instead
     * of the owner. The menu size is validated and the contents are set up as usual
     *
     * @param player the player to whom the menu will be opened
     */
    public void openMenu(Player player) {
        this.size = (getSize() < 9 || getSize() > 54) ? 54 : getSize();
        this.title = getTitle() != null ? getTitle() : "Undefined";

        inventory = Bukkit.createInventory(this, this.size, this.title);
        setupContents();

        if (player.isOnline()) {
            player.openInventory(inventory);
        }
    }

    /**
     * Retrieves the inventory associated with this menu
     *
     * @return the inventory of this menu
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
