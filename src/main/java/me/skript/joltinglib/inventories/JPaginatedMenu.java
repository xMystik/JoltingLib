package me.skript.joltinglib.inventories;

import me.skript.joltinglib.items.JItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class JPaginatedMenu extends JMenu {

    protected int currentPage;
    protected int nextPageSlot;
    protected int previousPageSlot;

    public JPaginatedMenu(Player player) {
        super(player);
    }

    public abstract int getNextPageSlot();
    public abstract int getPreviousPageSlot();
    public abstract ItemStack getPreviousPageItem();
    public abstract ItemStack getNextPageItem();
    public abstract int getItemsPerPage();
    public abstract List<ItemStack> getItemsToPaginate();
    public abstract ItemStack getFillerItem();
    public abstract void setupLayout();

    public void openMenu(Player player) {
        if(getSize() > 54 || getSize() < 9) {
            this.size = 54;
        }

        if(getTitle() == null) {
            this.title = "Undefined";
        }

        inventory = Bukkit.createInventory(this, getSize(), getTitle());

        setupLayout();
        setupContents();

        player.openInventory(inventory);
    }

    public void setupContents() {
        inventory.clear();

        setupLayout();

        if (getItemsToPaginate() == null) {
            return;
        }

        // Calculate the starting and ending indexes for the current page
        int start = currentPage * getItemsPerPage();
        int end = Math.min(start + getItemsPerPage(), getItemsToPaginate().size());

        // Populate the inventory with items for the current page
        for (int i = start; i < end; i++) {
            ItemStack item = getItemsToPaginate().get(i);
            inventory.addItem(item);
        }

        // Update the navigation items
        updateNavigationItems();
    }

    protected void updateNavigationItems() {
        inventory.setItem(inventory.getSize() - 9, null); // Previous page
        inventory.setItem(inventory.getSize() - 1, null); // Next page

        int nextPageSlot = getNextPageSlot();
        int previousPageSlot = getPreviousPageSlot();

        // Check if there's a previous page
        if (currentPage > 0) {
            // Add previous page item
            ItemStack previousPageItem = getPreviousPageItem();
            if (previousPageItem == null) {
                previousPageItem = new JItemBuilder(Material.IRON_DOOR)
                        .setDisplayName("&e&lPrevious Page")
                        .build();
            }
            inventory.setItem(previousPageSlot, previousPageItem);
        } else {
            // Use the filler item if provided or keep the slot empty
            ItemStack fillerItem = getFillerItem();
            if (fillerItem != null) {
                inventory.setItem(previousPageSlot, fillerItem);
            }
        }

        // Check if there's a next page
        int maxPage = calculateMaxPage();
        if (currentPage < maxPage - 1) {
            // Add next page item
            ItemStack nextPageItem = getNextPageItem();
            if (nextPageItem == null) {
                nextPageItem = new JItemBuilder(Material.ARROW)
                        .setDisplayName("&e&lNext Page")
                        .build();
            }
            inventory.setItem(nextPageSlot, nextPageItem);
        } else {
            // Use the filler item if provided, or keep the slot empty
            ItemStack fillerItem = getFillerItem();
            if (fillerItem != null) {
                inventory.setItem(nextPageSlot, fillerItem);
            }
        }
    }

    public void nextPage() {
        int maxPage = calculateMaxPage();

        if (currentPage < maxPage - 1) {
            currentPage++;
            setupContents();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setupContents();
        }
    }

    private int calculateMaxPage() {
        if (getItemsToPaginate() == null) {
            return 1;
        }

        return (int) Math.ceil((double) getItemsToPaginate().size() / getItemsPerPage());
    }

    public boolean hasNextPage() {
        return currentPage < calculateMaxPage() - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void goToPage(int pageNumber) {
        currentPage = pageNumber;
        setupContents();
    }
}
