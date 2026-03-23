package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public abstract class Gui {

    protected final EasyClans plugin;
    protected final Player player;

    protected Inventory inventory;
    protected InventoryView view;

    public Gui(EasyClans plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public abstract void build();

    public abstract void handleClick(InventoryClickEvent event);

    public void open() {
        build();
        plugin.getGuiManager().openGui(player, this);

        if (view != null) {
            player.openInventory(view);
            return;
        }

        if (inventory != null) {
            player.openInventory(inventory);
            return;
        }

        throw new IllegalStateException("GUI has neither inventory nor view set.");
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryView getView() {
        return view;
    }

    public Player getPlayer() {
        return player;
    }
}