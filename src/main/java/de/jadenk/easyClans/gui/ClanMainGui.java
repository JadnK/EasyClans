package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.util.InventoryBuilder;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanMainGui extends Gui {

    public ClanMainGui(EasyClans plugin, org.bukkit.entity.Player player) {
        super(plugin, player);
    }

    @Override
    public void build() {
        Clan clan = plugin.getClanService().getClan(player);

        InventoryBuilder builder = new InventoryBuilder(5, "&8☠ &bEasyClans &8| &7Menü")
                .decorateDefault();

        if (clan == null) {
            builder.addItem(new ItemBuilder(Material.NAME_TAG)
                    .setName("&aClan erstellen")
                    .setLore(
                            "&7Erstelle deinen eigenen Clan.",
                            "",
                            "&eKlick zum Festlegen des Namens",
                            "&7Im Amboss kannst du auch & nutzen."
                    )
                    .build(), 20);

            builder.addItem(new ItemBuilder(Material.BOOK)
                    .setName("&bÖffentliche Clans")
                    .setLore("&7Hier kannst du später öffentliche Clans browsen.")
                    .build(), 24);
        } else {
            builder.addItem(new ItemBuilder(Material.NETHER_STAR)
                    .setName("&bClan verwalten")
                    .setLore(
                            "&7Name: &f" + clan.getName(),
                            "&7Mitglieder: &f" + clan.getMembers().size(),
                            "",
                            "&eKlick zum Öffnen"
                    )
                    .build(), 22);
        }

        this.inventory = builder.build();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 20 -> new AnvilInputGui(plugin, player, "&8Clan Namen festlegen", "MeinClan") {
                @Override
                public void onComplete(String text) {
                    plugin.getLogger().info("[ClanDebug] onComplete reached in clan create GUI for "
                            + player.getName() + " text='" + text + "'");
                    if (text == null) {
                        player.sendMessage("§cUngültiger Clanname.");
                        return;
                    }

                    String finalName = text.trim();

                    if (finalName.isBlank()) {
                        player.sendMessage("§cUngültiger Clanname.");
                        return;
                    }

                    plugin.getClanService().createClan(player, finalName);
                }
            }.open();

            case 22 -> new ClanManageGui(plugin, player).open();
            case 24 -> player.sendMessage("§7Kommt gleich als nächstes.");
        }
    }
}