package de.jadenk.easyClans.service;

import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanInvite;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.InviteManager;

import java.util.UUID;

public class InviteService {

    private final ClanManager clanManager;
    private final InviteManager inviteManager;

    public InviteService(ClanManager clanManager, InviteManager inviteManager) {
        this.clanManager = clanManager;
        this.inviteManager = inviteManager;
    }

    public boolean sendInvite(UUID invitedPlayer, String clanName, UUID invitedBy) {
        if (invitedPlayer == null || clanName == null || clanName.isBlank() || invitedBy == null) {
            return false;
        }

        if (clanManager.hasClan(invitedPlayer)) {
            return false;
        }

        Clan clan = clanManager.getClan(clanName);

        if (clan == null) {
            return false;
        }

        ClanInvite invite = new ClanInvite(clan.getName(), invitedPlayer, invitedBy);
        inviteManager.addInvite(invite);
        return true;
    }

    public boolean acceptInvite(UUID player, String clanName) {
        if (player == null || clanName == null || clanName.isBlank()) {
            return false;
        }

        if (clanManager.hasClan(player)) {
            return false;
        }

        ClanInvite invite = inviteManager.getInvite(player);

        if (invite == null) {
            return false;
        }

        if (!invite.getClanName().equalsIgnoreCase(clanName)) {
            return false;
        }

        Clan clan = clanManager.getClan(clanName);

        if (clan == null) {
            inviteManager.removeInvite(player);
            return false;
        }

        clan.addMember(player);
        clanManager.addPlayerToClan(player, clan.getName());
        inviteManager.removeInvite(player);
        return true;
    }

    public ClanInvite getInvite(UUID player) {
        return inviteManager.getInvite(player);
    }

    public boolean hasInvite(UUID player) {
        return inviteManager.hasInvite(player);
    }

    public void removeInvite(UUID player) {
        inviteManager.removeInvite(player);
    }
}