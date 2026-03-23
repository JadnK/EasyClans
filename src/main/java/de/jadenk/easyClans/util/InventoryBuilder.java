package de.jadenk.easyClans.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private final Inventory inventory;

    public InventoryBuilder(int rows, String title) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title.replace("&", "§"));
    }

    public InventoryBuilder addItem(ItemStack item, int slot) {
        inventory.setItem(slot, item);
        return this;
    }

    public InventoryBuilder addItem(ItemStack item) {
        inventory.addItem(item);
        return this;
    }

    public InventoryBuilder addInvItem(ItemStack item, int line, int placeInLine) {
        int slot = (line - 1) * 9 + (placeInLine - 1);

        if (line < 1 || placeInLine < 1 || placeInLine > 9 || slot >= inventory.getSize()) {
            throw new IllegalArgumentException("Ungültige Position: line=" + line + ", placeInLine=" + placeInLine);
        }

        inventory.setItem(slot, item);
        return this;
    }

    public InventoryBuilder fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
        return this;
    }

    public InventoryBuilder fillEmptySlots(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
        return this;
    }

    public InventoryBuilder fillBorder(ItemStack item) {
        int size = inventory.getSize();
        int rows = size / 9;

        for (int slot = 0; slot < 9; slot++) {
            inventory.setItem(slot, item);
        }

        int startLastRow = (rows - 1) * 9;
        for (int slot = startLastRow; slot < size; slot++) {
            inventory.setItem(slot, item);
        }

        for (int row = 0; row < rows; row++) {
            inventory.setItem(row * 9, item);
            inventory.setItem(row * 9 + 8, item);
        }

        return this;
    }

    public InventoryBuilder clearSlot(int slot) {
        inventory.setItem(slot, null);
        return this;
    }

    public InventoryBuilder clearLine(int line) {
        if (line < 1) {
            throw new IllegalArgumentException("Line muss mindestens 1 sein.");
        }

        int startSlot = (line - 1) * 9;
        int endSlot = startSlot + 8;

        if (endSlot >= inventory.getSize()) {
            throw new IllegalArgumentException("Line " + line + " existiert in diesem Inventory nicht.");
        }

        for (int i = startSlot; i <= endSlot; i++) {
            inventory.setItem(i, null);
        }

        return this;
    }

    public InventoryBuilder decorateDefault() {
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName("&7")
                .hideAllFlags()
                .build();

        return fillBorder(filler).fillEmptySlots(filler);
    }

    public Inventory build() {
        return inventory;
    }
}