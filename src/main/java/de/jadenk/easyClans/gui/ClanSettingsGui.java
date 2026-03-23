package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.util.InventoryBuilder;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanSettingsGui extends Gui {

    public ClanSettingsGui(EasyClans plugin, org.bukkit.entity.Player player) {
        super(plugin, player);
    }

    @Override
    public void build() {
        Clan clan = plugin.getClanService().getClan(player);

        if (clan == null) {
            new ClanMainGui(plugin, player).open();
            return;
        }

        this.inventory = new InventoryBuilder(4, "&8⚙ &bClan Einstellungen")
                .decorateDefault()
                .addItem(new ItemBuilder(Material.PAPER)
                        .setName("&bClan Chat")
                        .setLore("&7Aktiv: " + bool(clan.getSettings().isClanChatEnabled()), "&eKlick zum Umschalten")
                        .build(), 11)
                .addItem(new ItemBuilder(Material.IRON_SWORD)
                        .setName("&cFriendly Fire")
                        .setLore("&7Aktiv: " + bool(clan.getSettings().isFriendlyFire()), "&eKlick zum Umschalten")
                        .build(), 13)
                .addItem(new ItemBuilder(Material.OAK_FENCE_GATE)
                        .setName("&aPublic Join")
                        .setLore("&7Aktiv: " + bool(clan.getSettings().isPublicJoin()), "&eKlick zum Umschalten")
                        .build(), 15)
                .addItem(new ItemBuilder(Material.BARRIER)
                        .setName("&cZurück")
                        .build(), 31)
                .build();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 11 -> plugin.getClanService().toggleClanChatSetting(player);
            case 13 -> plugin.getClanService().toggleFriendlyFire(player);
            case 15 -> plugin.getClanService().togglePublicJoin(player);
            case 31 -> {
                new ClanManageGui(plugin, player).open();
                return;
            }
            default -> {
                return;
            }
        }

        new ClanSettingsGui(plugin, player).open();
    }

    private String bool(boolean value) {
        return value ? "§aAN" : "§cAUS";
    }
}