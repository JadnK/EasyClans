package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

public abstract class AnvilInputGui extends Gui {

    private final String title;
    private final String defaultText;
    private String currentText;

    public AnvilInputGui(EasyClans plugin, Player player, String title, String defaultText) {
        super(plugin, player);
        this.title = title.replace("&", "§");
        this.defaultText = defaultText.replace("&", "§");
        this.currentText = this.defaultText;
    }

    @Override
    public void build() {
        this.view = MenuType.ANVIL.create(player, title.replace("§", ""));
        this.inventory = view.getTopInventory();

        ItemStack input = new ItemBuilder(Material.PAPER)
                .setName(defaultText)
                .setLore("&7Gib hier deinen Clan-Namen ein.")
                .build();

        inventory.setItem(0, input);

        plugin.getGuiManager().openAnvil(player, this);

        plugin.getLogger().info("[ClanDebug] Opened anvil GUI for " + player.getName()
                + " title='" + title + "' defaultText='" + defaultText + "'");
    }

    @Override
    public void open() {
        build();
        plugin.getGuiManager().openAnvil(player, this);
        player.openInventory(view);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        plugin.getLogger().info("[ClanDebug] AnvilInputGui#handleClick called for " + player.getName()
                + " rawSlot=" + event.getRawSlot()
                + " click=" + event.getClick()
                + " action=" + event.getAction());

        if (!(event.getWhoClicked() instanceof Player clicker)) {
            return;
        }

        if (event.getClickedInventory() == null) {
            plugin.getLogger().info("[ClanDebug] Click ignored because clicked inventory is null.");
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            plugin.getLogger().info("[ClanDebug] Blocked invalid anvil interaction for " + clicker.getName());
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            plugin.getLogger().info("[ClanDebug] Blocked bottom inventory interaction for " + clicker.getName());
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (event.getRawSlot() != 2) {
            plugin.getLogger().info("[ClanDebug] Click was not on result slot. rawSlot=" + event.getRawSlot());
            return;
        }

        String text = currentText;
        plugin.getLogger().info("[ClanDebug] Using cached currentText='" + text + "'");

        if (text == null || text.isBlank()) {
            text = defaultText;
        }

        text = text.replace("&", "§").trim();

        if (text.isBlank()) {
            plugin.getLogger().info("[ClanDebug] Final anvil text is blank, aborting.");
            return;
        }

        currentText = text;

        plugin.getLogger().info("[ClanDebug] Result slot accepted by " + clicker.getName()
                + " finalText='" + text + "'");

        plugin.getGuiManager().closeAnvil(clicker);
        clicker.closeInventory();

        final String finalText = text;
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getLogger().info("[ClanDebug] Running anvil onComplete for " + clicker.getName()
                    + " with text='" + finalText + "'");
            onComplete(finalText);
        });
    }

    public ItemStack createResultItem(String text) {
        this.currentText = (text == null || text.isBlank())
                ? defaultText
                : text.replace("&", "§").trim();

        plugin.getLogger().info("[ClanDebug] Updating anvil result item for " + player.getName()
                + " to text='" + currentText + "'");

        return new ItemBuilder(Material.PAPER)
                .setName(currentText)
                .setLore("&aKlicke zum Bestätigen")
                .build();
    }

    public String getCurrentText() {
        return currentText;
    }

    public abstract void onComplete(String text);
}