package de.jadenk.easyClans.listener;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.gui.AnvilInputGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilGuiListener implements Listener {

    private final EasyClans plugin;

    public AnvilGuiListener(EasyClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }

        AnvilInputGui gui = plugin.getGuiManager().getOpenAnvil(player);
        if (gui == null) {
            return;
        }

        String renameText = null;
        try {
            renameText = event.getView().getRenameText();
        } catch (Throwable ignored) {
        }

        plugin.getLogger().info("[ClanDebug] PrepareAnvilEvent fired for " + player.getName()
                + " renameText='" + renameText + "'");

        ItemStack result = gui.createResultItem(renameText);
        event.setResult(result);

        try {
            event.getView().setRepairCost(0);
            event.getView().setRepairItemCountCost(0);
            event.getView().setMaximumRepairCost(Integer.MAX_VALUE);
        } catch (Throwable ignored) {
        }
    }
}