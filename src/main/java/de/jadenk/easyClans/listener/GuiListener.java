package de.jadenk.easyClans.listener;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.gui.AnvilInputGui;
import de.jadenk.easyClans.gui.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {

    private final EasyClans plugin;

    public GuiListener(EasyClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        plugin.getLogger().info("[ClanDebug] GuiListener click from " + player.getName()
                + " topType=" + event.getView().getTopInventory().getType()
                + " rawSlot=" + event.getRawSlot()
                + " click=" + event.getClick()
                + " action=" + event.getAction());

        Gui gui = plugin.getGuiManager().getOpenGui(player);
        if (gui != null && event.getView().getTopInventory().equals(gui.getInventory())) {
            plugin.getLogger().info("[ClanDebug] Forwarding click to normal GUI for " + player.getName());
            gui.handleClick(event);
            return;
        }

        AnvilInputGui anvilGui = plugin.getGuiManager().getOpenAnvil(player);
        if (anvilGui != null && event.getView().getTopInventory().equals(anvilGui.getInventory())) {
            plugin.getLogger().info("[ClanDebug] Forwarding click to anvil GUI for " + player.getName());
            anvilGui.handleClick(event);
            return;
        }

        plugin.getLogger().info("[ClanDebug] No matching GUI found for click by " + player.getName());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Gui gui = plugin.getGuiManager().getOpenGui(player);
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            plugin.getLogger().info("[ClanDebug] Closing normal GUI for " + player.getName());
            plugin.getGuiManager().closeGui(player);
        }

        AnvilInputGui anvilGui = plugin.getGuiManager().getOpenAnvil(player);
        if (anvilGui != null && event.getInventory().equals(anvilGui.getInventory())) {
            plugin.getLogger().info("[ClanDebug] Closing anvil GUI for " + player.getName());
            plugin.getGuiManager().closeAnvil(player);
        }
    }
}