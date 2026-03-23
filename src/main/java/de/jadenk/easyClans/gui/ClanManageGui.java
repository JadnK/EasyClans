package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.util.InventoryBuilder;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanManageGui extends Gui {

    public ClanManageGui(EasyClans plugin, org.bukkit.entity.Player player) {
        super(plugin, player);
    }

    @Override
    public void build() {
        Clan clan = plugin.getClanService().getClan(player);

        if (clan == null) {
            new ClanMainGui(plugin, player).open();
            return;
        }

        this.inventory = new InventoryBuilder(5, "&8⚔ &bClan Management")
                .decorateDefault()
                .addItem(new ItemBuilder(Material.NAME_TAG)
                        .setName("&bClan Info")
                        .setLore(
                                "&7Name: &f" + clan.getName(),
                                "&7Punkte: &f" + clan.getPoints(),
                                "&7Level: &f" + clan.getLevel()
                        )
                        .build(), 13)
                .addItem(new ItemBuilder(Material.PLAYER_HEAD)
                        .setName("&aMitglieder verwalten")
                        .setLore("&7Mitglieder ansehen, promoten, demoten, kicken.")
                        .build(), 20)
                .addItem(new ItemBuilder(Material.COMPARATOR)
                        .setName("&eEinstellungen")
                        .setLore("&7Friendly Fire, Clan Chat, Public Join.")
                        .build(), 22)
                .addItem(new ItemBuilder(Material.OAK_DOOR)
                        .setName("&6Clan verlassen")
                        .setLore("&7Verlasse deinen Clan.")
                        .build(), 24)
                .addItem(new ItemBuilder(Material.BARRIER)
                        .setName("&cZurück")
                        .setLore("&7Zurück zum Hauptmenü.")
                        .build(), 40)
                .build();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 20 -> new ClanMembersGui(plugin, player).open();
            case 22 -> new ClanSettingsGui(plugin, player).open();
            case 24 -> {
                plugin.getClanService().leaveClan(player);
                player.closeInventory();
            }
            case 40 -> new ClanMainGui(plugin, player).open();
        }
    }
}