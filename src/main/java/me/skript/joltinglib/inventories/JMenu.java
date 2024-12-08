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

    public abstract int getSize();
    public abstract String getTitle();
    public abstract void setupContents();
    public abstract void handleClicks(InventoryClickEvent event);

    public JMenu(Player player) {
        owner = player.getUniqueId();
    }

    public void openMenu() {
        if(getSize() > 54 || getSize() < 9) {
            this.size = 54;
        }

        if(getTitle() == null) {
            this.title = "Undefined";
        }

        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        setupContents();

        if(Bukkit.getPlayer(owner).isOnline()) {
            Bukkit.getPlayer(owner).openInventory(inventory);
        }
    }

    public void openMenu(Player sender) {
        if(getSize() > 54 || getSize() < 9) {
            this.size = 54;
        }

        if(getTitle() == null) {
            this.title = "Undefined";
        }

        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        setupContents();

        if(sender.isOnline()) {
            sender.openInventory(inventory);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
