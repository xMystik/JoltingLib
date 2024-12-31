package me.skript.joltinglib.inventories;

import me.skript.joltinglib.colorcodes.JText;
import me.skript.joltinglib.items.JItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class JPaginatedMenu extends JMenu {

    protected int currentPage;

    /**
     * Creates a new paginated menu for the specified player
     * <p>
     * This class extends {@link JMenu} and adds pagination functionality. It allows
     * creating a menu with multiple pages and handling navigation between pages
     *
     * @param player the player who will own this paginated menu
     */
    public JPaginatedMenu(Player player) {
        super(player);
    }

    /**
     * Retrieves the slot index for the next page navigation item
     *
     * @return the slot index for the next page item
     */
    public abstract int getNextPageSlot();

    /**
     * Retrieves the slot index for the previous page navigation item
     *
     * @return the slot index for the previous page item
     */
    public abstract int getPreviousPageSlot();

    /**
     * Retrieves the item to display for the previous page navigation
     *
     * @return the item to display for the previous page button
     */
    public abstract ItemStack getPreviousPageItem();

    /**
     * Retrieves the item to display for the next page navigation
     *
     * @return the item to display for the next page button
     */
    public abstract ItemStack getNextPageItem();

    /**
     * Retrieves the number of items to display per page in the inventory
     *
     * @return the number of items per page
     */
    public abstract int getItemsPerPage();

    /**
     * Retrieves the list of items to paginate across the menu pages
     *
     * @return a list of items to be paginated
     */
    public abstract List<ItemStack> getItemsToPaginate();

    /**
     * Retrieves the list of slots that can be used for pagination
     * If null or empty, all empty slots will be used
     *
     * @return the list of slots for pagination, or null to use all empty slots
     */
    public abstract List<Integer> getPaginationSlots();

    /**
     * Sets up the layout of the menu, such as configuring item placements
     * for pagination and navigation buttons
     */
    public abstract void setupLayout();

    /**
     * Opens the menu for the specified player, ensuring the size and title are valid.
     * The layout and contents are set up, and the inventory is opened for the player.
     *
     * @param player the player to whom the menu will be opened
     */
    @Override
    public void openMenu(Player player) {
        this.size = (getSize() < 9 || getSize() > 54) ? 54 : getSize();
        this.title = JText.format(getTitle());

        inventory = Bukkit.createInventory(this, this.size, this.title);

        setupLayout();
        setupContents();

        if (player.isOnline()) {
            player.openInventory(inventory);
        }
    }

    /**
     * Sets up the contents of the menu, including pagination logic. It clears the
     * inventory, populates it with the items for the current page, and updates the
     * navigation items (previous/next page buttons)
     */
    public void setupContents() {
        inventory.clear();
        setupLayout();

        if (getItemsToPaginate() == null) {
            return;
        }

        List<Integer> paginationSlots = getPaginationSlots();
        boolean useAllEmptySlots = (paginationSlots == null || paginationSlots.isEmpty());

        int start = currentPage * getItemsPerPage();
        int end = Math.min(start + getItemsPerPage(), getItemsToPaginate().size());

        List<ItemStack> itemsToPaginate = getItemsToPaginate().subList(start, end);

        if (useAllEmptySlots) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null && !itemsToPaginate.isEmpty()) {
                    inventory.setItem(i, itemsToPaginate.remove(0));
                }
            }
        } else {
            for (int slot : paginationSlots) {
                if (slot >= 0 && slot < inventory.getSize() && !itemsToPaginate.isEmpty()) {
                    inventory.setItem(slot, itemsToPaginate.remove(0));
                }
            }
        }

        updateNavigationItems();
    }

    /**
     * Updates the previous and next page items based on the current page.
     * If a previous or next page exists, the corresponding item is placed in
     * the inventory.
     */
    protected void updateNavigationItems() {
        int nextPageSlot = getNextPageSlot();
        int previousPageSlot = getPreviousPageSlot();

        if (currentPage > 0) {
            ItemStack previousPageItem = getPreviousPageItem();
            if (previousPageItem == null) {
                previousPageItem = new JItemBuilder(Material.ARROW)
                        .setDisplayName("<yellow><bold>Previous Page")
                        .build();
            }
            inventory.setItem(previousPageSlot, previousPageItem);
        } else {
            inventory.setItem(previousPageSlot, null);
        }

        int maxPage = calculateMaxPage();
        if (currentPage < maxPage - 1) {
            ItemStack nextPageItem = getNextPageItem();
            if (nextPageItem == null) {
                nextPageItem = new JItemBuilder(Material.ARROW)
                        .setDisplayName("<yellow><bold>Next Page")
                        .build();
            }
            inventory.setItem(nextPageSlot, nextPageItem);
        } else {
            inventory.setItem(nextPageSlot, null);
        }
    }

    /**
     * Advances to the next page, if one exists
     * <p>
     * If the current page is not the last page, the current page is incremented
     * and the contents are reloaded for the new page
     */
    public void nextPage() {
        int maxPage = calculateMaxPage();
        if (currentPage < maxPage - 1) {
            currentPage++;
            setupContents();
        }
    }

    /**
     * Goes back to the previous page, if one exists
     * <p>
     * If the current page is not the first page, the current page is decremented
     * and the contents are reloaded for the new page
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setupContents();
        }
    }

    /**
     * Calculates the maximum number of pages based on the total number of items
     * and the number of items per page
     *
     * @return the total number of pages
     */
    private int calculateMaxPage() {
        if (getItemsToPaginate() == null) {
            return 1;
        }
        return (int) Math.ceil((double) getItemsToPaginate().size() / getItemsPerPage());
    }

    /**
     * Checks if there is a next page available
     *
     * @return true if a next page exists, false otherwise
     */
    public boolean hasNextPage() {
        return currentPage < calculateMaxPage() - 1;
    }

    /**
     * Checks if there is a previous page available
     *
     * @return true if a previous page exists, false otherwise
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * Navigates directly to a specific page
     *
     * @param pageNumber the page number to go to
     */
    public void moveToPage(int pageNumber) {
        currentPage = pageNumber;
        setupContents();
    }
}
