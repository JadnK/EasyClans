package de.jadenk.easyClans.gui;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanRole;
import de.jadenk.easyClans.util.InventoryBuilder;
import de.jadenk.easyClans.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanMembersGui extends Gui {

    private final List<UUID> shownMembers = new ArrayList<>();

    public ClanMembersGui(EasyClans plugin, org.bukkit.entity.Player player) {
        super(plugin, player);
    }

    @Override
    public void build() {
        Clan clan = plugin.getClanService().getClan(player);

        if (clan == null) {
            new ClanMainGui(plugin, player).open();
            return;
        }

        InventoryBuilder builder = new InventoryBuilder(6, "&8👥 &bClan Mitglieder")
                .decorateDefault();

        shownMembers.clear();

        int slot = 10;
        for (UUID memberId : clan.getMembers().keySet()) {
            if (slot >= 44) {
                break;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberId);
            ClanRole role = clan.getRole(memberId);

            builder.addItem(new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(offlinePlayer)
                    .setName("&b" + offlinePlayer.getName())
                    .setLore(
                            "&7Rolle: &f" + role.name(),
                            "&7UUID: &8" + memberId,
                            "",
                            "&eKlick zum Verwalten"
                    )
                    .build(), slot);

            shownMembers.add(memberId);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
        }

        builder.addItem(new ItemBuilder(Material.BARRIER)
                .setName("&cZurück")
                .build(), 49);

        this.inventory = builder.build();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getRawSlot() == 49) {
            new ClanManageGui(plugin, player).open();
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.PLAYER_HEAD) {
            return;
        }

        String displayName = event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "");
        org.bukkit.entity.Player target = Bukkit.getPlayerExact(displayName);

        if (target == null) {
            player.sendMessage("§cDer Spieler ist gerade offline.");
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cDu kannst dich hier nicht selbst verwalten.");
            return;
        }

        new ClanMemberActionGui(plugin, player, target).open();
    }
}