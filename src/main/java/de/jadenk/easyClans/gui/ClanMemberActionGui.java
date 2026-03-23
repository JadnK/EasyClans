package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanRole;
import de.jadenk.easyClans.util.InventoryBuilder;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanMemberActionGui extends Gui {

    private final Player target;

    public ClanMemberActionGui(EasyClans plugin, Player player, Player target) {
        super(plugin, player);
        this.target = target;
    }

    @Override
    public void build() {
        Clan clan = plugin.getClanService().getClan(player);
        ClanRole role = clan == null ? null : clan.getRole(target.getUniqueId());

        this.inventory = new InventoryBuilder(4, "&8⚙ &bMitglied verwalten")
                .decorateDefault()
                .addItem(new ItemBuilder(Material.PLAYER_HEAD)
                        .setSkullOwner(target)
                        .setName("&b" + target.getName())
                        .setLore("&7Rolle: &f" + (role == null ? "UNBEKANNT" : role.name()))
                        .build(), 13)
                .addItem(new ItemBuilder(Material.EMERALD)
                        .setName("&aBefördern")
                        .setLore("&7Setzt den Spieler auf Moderator.")
                        .build(), 20)
                .addItem(new ItemBuilder(Material.REDSTONE)
                        .setName("&cDegradieren")
                        .setLore("&7Setzt den Spieler auf Member.")
                        .build(), 22)
                .addItem(new ItemBuilder(Material.LAVA_BUCKET)
                        .setName("&4Kicken")
                        .setLore("&7Entfernt den Spieler aus dem Clan.")
                        .build(), 24)
                .addItem(new ItemBuilder(Material.BARRIER)
                        .setName("&cZurück")
                        .build(), 31)
                .build();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 20 -> {
                plugin.getClanService().promoteMember(player, target);
                new ClanMemberActionGui(plugin, player, target).open();
            }
            case 22 -> {
                plugin.getClanService().demoteMember(player, target);
                new ClanMemberActionGui(plugin, player, target).open();
            }
            case 24 -> {
                plugin.getClanService().kickMember(player, target, "Über GUI entfernt");
                new ClanMembersGui(plugin, player).open();
            }
            case 31 -> new ClanMembersGui(plugin, player).open();
        }
    }
}