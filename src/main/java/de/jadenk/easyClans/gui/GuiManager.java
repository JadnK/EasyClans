package de.jadenk.easyClans.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private final Map<UUID, Gui> openGuis = new HashMap<>();
    private final Map<UUID, AnvilInputGui> openAnvils = new HashMap<>();

    public void openGui(Player player, Gui gui) {
        openGuis.put(player.getUniqueId(), gui);
    }

    public Gui getOpenGui(Player player) {
        return openGuis.get(player.getUniqueId());
    }

    public void closeGui(Player player) {
        openGuis.remove(player.getUniqueId());
    }

    public void openAnvil(Player player, AnvilInputGui gui) {
        openAnvils.put(player.getUniqueId(), gui);
    }

    public AnvilInputGui getOpenAnvil(Player player) {
        return openAnvils.get(player.getUniqueId());
    }

    public void closeAnvil(Player player) {
        openAnvils.remove(player.getUniqueId());
    }

    public void closeAll(Player player) {
        closeGui(player);
        closeAnvil(player);
    }
}