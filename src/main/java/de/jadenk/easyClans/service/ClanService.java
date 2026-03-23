package de.jadenk.easyClans.service;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanRole;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.InviteManager;
import de.jadenk.easyClans.manager.PlayerDataManager;
import de.jadenk.easyClans.storage.model.PlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanService {

    private final EasyClans plugin;

    private final ClanManager clanManager;
    private final InviteManager inviteManager;
    private final PlayerDataManager playerDataManager;
    private final PermissionService permissionService;
    private final InviteService inviteService;

    public ClanService(EasyClans plugin, ClanManager clanManager, InviteManager inviteManager, PlayerDataManager playerDataManager) {
        this.clanManager = clanManager;
        this.plugin = plugin;
        this.inviteManager = inviteManager;
        this.playerDataManager = playerDataManager;
        this.permissionService = new PermissionService();
        this.inviteService = new InviteService(clanManager, inviteManager);
    }

    private void persist() {
        if (plugin.getStorage() != null) {
            plugin.getStorage().save();
        }
    }

    public void createClan(Player player, String name) {
        if (player == null || name == null || name.isBlank()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (clanManager.hasClan(uuid)) {
            player.sendMessage("§cDu bist bereits in einem Clan.");
            return;
        }

        if (clanManager.existsClan(name)) {
            player.sendMessage("§cDieser Clan existiert bereits.");
            return;
        }

        Clan clan = clanManager.createClan(name, uuid);

        if (clan == null) {
            player.sendMessage("§cClan konnte nicht erstellt werden.");
            return;
        }

        PlayerData playerData = playerDataManager.getOrCreate(uuid);
        playerData.setClanName(clan.getName());
        playerData.setRole(ClanRole.OWNER);
        playerData.setJoinedAt(System.currentTimeMillis());
        playerDataManager.save(playerData);

        persist();

        player.sendMessage("§aClan §e" + clan.getName() + " §aerfolgreich erstellt.");
    }

    public void invitePlayer(Player inviter, Player target) {
        if (inviter == null || target == null) {
            return;
        }

        UUID inviterId = inviter.getUniqueId();
        UUID targetId = target.getUniqueId();

        Clan clan = clanManager.getClanByPlayer(inviterId);

        if (clan == null) {
            inviter.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!permissionService.canInvite(clan, inviterId)) {
            inviter.sendMessage("§cDu darfst keine Spieler einladen.");
            return;
        }

        if (clanManager.hasClan(targetId)) {
            inviter.sendMessage("§cDieser Spieler ist bereits in einem Clan.");
            return;
        }

        if (inviteManager.hasInvite(targetId)) {
            inviter.sendMessage("§cDieser Spieler hat bereits eine offene Einladung.");
            return;
        }

        boolean success = inviteService.sendInvite(targetId, clan.getName(), inviterId);

        if (!success) {
            inviter.sendMessage("§cEinladung konnte nicht gesendet werden.");
            return;
        }

        inviter.sendMessage("§aEinladung an §e" + target.getName() + " §agesendet.");
        target.sendMessage("§aDu wurdest in den Clan §e" + clan.getName() + " §aeingeladen.");
        target.sendMessage("§7Benutze §e/clan join " + clan.getName());
    }

    public void joinClan(Player player, String clanName) {
        if (player == null || clanName == null || clanName.isBlank()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (clanManager.hasClan(uuid)) {
            player.sendMessage("§cDu bist bereits in einem Clan.");
            return;
        }

        boolean success = inviteService.acceptInvite(uuid, clanName);

        if (!success) {
            player.sendMessage("§cDu hast keine gültige Einladung für diesen Clan.");
            return;
        }

        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            player.sendMessage("§cClan konnte nicht gefunden werden.");
            return;
        }

        PlayerData playerData = playerDataManager.getOrCreate(uuid);
        playerData.setClanName(clan.getName());
        playerData.setRole(ClanRole.MEMBER);
        playerData.setJoinedAt(System.currentTimeMillis());
        playerData.setLastKickReason(null);
        playerDataManager.save(playerData);

        player.sendMessage("§aDu bist dem Clan §e" + clanName + " §abeigetreten.");
    }

    public void leaveClan(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Clan clan = clanManager.getClanByPlayer(uuid);

        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!permissionService.canLeave(clan, uuid)) {
            player.sendMessage("§cAls Owner kannst du den Clan nicht einfach verlassen.");
            return;
        }

        clan.removeMember(uuid);
        clanManager.removePlayerFromClan(uuid);

        PlayerData playerData = playerDataManager.getOrCreate(uuid);
        playerData.clearClanData();
        playerDataManager.save(playerData);

        player.sendMessage("§aDu hast den Clan §e" + clan.getName() + " §averlassen.");
    }

    public void kickMember(Player executor, Player target, String reason) {
        if (executor == null || target == null) {
            return;
        }

        UUID executorId = executor.getUniqueId();
        UUID targetId = target.getUniqueId();

        Clan clan = clanManager.getClanByPlayer(executorId);

        if (clan == null) {
            executor.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        Clan targetClan = clanManager.getClanByPlayer(targetId);

        if (targetClan == null || !clan.getName().equalsIgnoreCase(targetClan.getName())) {
            executor.sendMessage("§cDer Spieler ist nicht in deinem Clan.");
            return;
        }

        if (!permissionService.canKick(clan, executorId, targetId)) {
            executor.sendMessage("§cDu darfst diesen Spieler nicht kicken.");
            return;
        }

        clan.removeMember(targetId);
        clanManager.removePlayerFromClan(targetId);

        PlayerData playerData = playerDataManager.getOrCreate(targetId);
        playerData.clearClanData();
        playerData.setLastKickReason(reason);
        playerDataManager.save(playerData);

        executor.sendMessage("§aSpieler §e" + target.getName() + " §awurde gekickt.");
        target.sendMessage("§cDu wurdest aus dem Clan §e" + clan.getName() + " §centfernt.");

        if (reason != null && !reason.isBlank()) {
            target.sendMessage("§7Grund: §f" + reason);
        }
    }

    public void promoteMember(Player executor, Player target) {
        if (executor == null || target == null) {
            return;
        }

        UUID executorId = executor.getUniqueId();
        UUID targetId = target.getUniqueId();

        Clan clan = clanManager.getClanByPlayer(executorId);

        if (clan == null) {
            executor.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        Clan targetClan = clanManager.getClanByPlayer(targetId);

        if (targetClan == null || !clan.getName().equalsIgnoreCase(targetClan.getName())) {
            executor.sendMessage("§cDer Spieler ist nicht in deinem Clan.");
            return;
        }

        if (!permissionService.canPromote(clan, executorId, targetId)) {
            executor.sendMessage("§cDu darfst diesen Spieler nicht befördern.");
            return;
        }

        if (clan.getRole(targetId) == ClanRole.MODERATOR) {
            executor.sendMessage("§cDer Spieler ist bereits Moderator.");
            return;
        }

        clan.setRole(targetId, ClanRole.MODERATOR);

        PlayerData playerData = playerDataManager.getOrCreate(targetId);
        playerData.setClanName(clan.getName());
        playerData.setRole(ClanRole.MODERATOR);
        if (playerData.getJoinedAt() <= 0L) {
            playerData.setJoinedAt(System.currentTimeMillis());
        }
        playerDataManager.save(playerData);

        executor.sendMessage("§aSpieler §e" + target.getName() + " §awurde befördert.");
        target.sendMessage("§aDu wurdest im Clan §e" + clan.getName() + " §abefördert.");
    }

    public void demoteMember(Player executor, Player target) {
        if (executor == null || target == null) {
            return;
        }

        UUID executorId = executor.getUniqueId();
        UUID targetId = target.getUniqueId();

        Clan clan = clanManager.getClanByPlayer(executorId);

        if (clan == null) {
            executor.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        Clan targetClan = clanManager.getClanByPlayer(targetId);

        if (targetClan == null || !clan.getName().equalsIgnoreCase(targetClan.getName())) {
            executor.sendMessage("§cDer Spieler ist nicht in deinem Clan.");
            return;
        }

        if (!permissionService.canDemote(clan, executorId, targetId)) {
            executor.sendMessage("§cDu darfst diesen Spieler nicht degradieren.");
            return;
        }

        if (clan.getRole(targetId) != ClanRole.MODERATOR) {
            executor.sendMessage("§cDer Spieler ist kein Moderator.");
            return;
        }

        clan.setRole(targetId, ClanRole.MEMBER);

        PlayerData playerData = playerDataManager.getOrCreate(targetId);
        playerData.setClanName(clan.getName());
        playerData.setRole(ClanRole.MEMBER);
        if (playerData.getJoinedAt() <= 0L) {
            playerData.setJoinedAt(System.currentTimeMillis());
        }
        playerDataManager.save(playerData);

        executor.sendMessage("§aSpieler §e" + target.getName() + " §awurde degradiert.");
        target.sendMessage("§cDu wurdest im Clan §e" + clan.getName() + " §cdegradiert.");
    }

    public void deleteClan(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Clan clan = clanManager.getClanByPlayer(uuid);

        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!permissionService.canDelete(clan, uuid)) {
            player.sendMessage("§cNur der Owner darf den Clan löschen.");
            return;
        }

        for (UUID memberId : clan.getMembers().keySet()) {
            PlayerData playerData = playerDataManager.getOrCreate(memberId);
            playerData.clearClanData();
            playerDataManager.save(playerData);
        }

        String clanName = clan.getName();
        clanManager.deleteClan(clanName);

        persist();

        player.sendMessage("§aClan §e" + clanName + " §awurde gelöscht.");
    }

    public Clan getClan(Player player) {
        if (player == null) {
            return null;
        }

        return clanManager.getClanByPlayer(player.getUniqueId());
    }

    public void toggleClanChatSetting(Player player) {
        Clan clan = getClan(player);

        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!clan.isOwner(player.getUniqueId())) {
            player.sendMessage("§cNur der Owner kann das ändern.");
            return;
        }

        clan.getSettings().setClanChatEnabled(!clan.getSettings().isClanChatEnabled());
        player.sendMessage("§aClan Chat ist jetzt " + (clan.getSettings().isClanChatEnabled() ? "§aAN" : "§cAUS"));
    }

    public void toggleFriendlyFire(Player player) {
        Clan clan = getClan(player);

        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!clan.isOwner(player.getUniqueId())) {
            player.sendMessage("§cNur der Owner kann das ändern.");
            return;
        }

        clan.getSettings().setFriendlyFire(!clan.getSettings().isFriendlyFire());
        player.sendMessage("§aFriendly Fire ist jetzt " + (clan.getSettings().isFriendlyFire() ? "§aAN" : "§cAUS"));
    }

    public void togglePublicJoin(Player player) {
        Clan clan = getClan(player);

        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan.");
            return;
        }

        if (!clan.isOwner(player.getUniqueId())) {
            player.sendMessage("§cNur der Owner kann das ändern.");
            return;
        }

        clan.getSettings().setPublicJoin(!clan.getSettings().isPublicJoin());
        player.sendMessage("§aPublic Join ist jetzt " + (clan.getSettings().isPublicJoin() ? "§aAN" : "§cAUS"));
    }
}