package de.jadenk.easyClans.commands;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanInvite;
import de.jadenk.easyClans.gui.ClanMainGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {

    private final EasyClans plugin;

    public ClanCommand(EasyClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Command benutzen.");
            return true;
        }


        if (args.length == 0) {new ClanMainGui(plugin, player).open(); }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("invite")) {

                if (args.length < 2) {
                    player.sendMessage("§c/clan invite <spieler>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cSpieler nicht gefunden");
                    return true;
                }

                Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
                if (clan == null) {
                    player.sendMessage("§cDu bist in keinem Clan");
                    return true;
                }

                if (plugin.getClanManager().getClanByPlayer(target.getUniqueId()) != null) {
                    player.sendMessage("§cSpieler ist bereits in einem Clan");
                    return true;
                }

                plugin.getInviteManager().addInvite(
                        target.getUniqueId(),
                        clan.getName(),
                        player.getUniqueId()
                );

                target.sendMessage("§aDu wurdest in den Clan §e" + clan.getName());
                target.sendMessage("§7/clan accept §8| §7/clan deny");

                player.sendMessage("§aEinladung gesendet!");

                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {

                ClanInvite invite = plugin.getInviteManager().getInvite(player.getUniqueId());

                if (invite == null) {
                    player.sendMessage("§cKeine Einladung vorhanden");
                    return true;
                }

                Clan clan = plugin.getClanManager().getClan(invite.getClanName());
                if (clan == null) {
                    player.sendMessage("§cClan existiert nicht mehr");
                    return true;
                }

                plugin.getClanService().joinClan(player, clan.getName());
//                clan.addMember(player.getUniqueId());

                plugin.getInviteManager().removeInvite(player.getUniqueId());

                player.sendMessage("§aDu bist dem Clan §e" + clan.getName() + " §abeigetreten!");

                return true;
            }


            if (args[0].equalsIgnoreCase("delete")) {


                plugin.getClanService().deleteClan(player);

                player.sendMessage("§cgelöscht");

                return true;
            }

            if (args[0].equalsIgnoreCase("deny")) {

                if (!plugin.getInviteManager().hasInvite(player.getUniqueId())) {
                    player.sendMessage("§cKeine Einladung vorhanden");
                    return true;
                }

                plugin.getInviteManager().removeInvite(player.getUniqueId());

                player.sendMessage("§cEinladung abgelehnt");

                return true;
            }
        }
        return true;
    }
}