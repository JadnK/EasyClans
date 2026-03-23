package de.jadenk.easyClans.manager;

import de.jadenk.easyClans.clan.ClanInvite;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {

    private final Map<UUID, ClanInvite> invites;

    public InviteManager() {
        this.invites = new HashMap<>();
    }

    public void addInvite(ClanInvite invite) {
        if (invite == null || invite.getInvitedPlayer() == null) {
            return;
        }

        invites.put(invite.getInvitedPlayer(), invite);
    }

    public void addInvite(UUID player, String clanName, UUID invitedBy) {
        if (player == null || clanName == null || clanName.isBlank() || invitedBy == null) {
            return;
        }

        ClanInvite invite = new ClanInvite(clanName, player, invitedBy);
        invites.put(player, invite);
    }

    public ClanInvite getInvite(UUID player) {
        if (player == null) {
            return null;
        }

        ClanInvite invite = invites.get(player);

        if (invite == null) {
            return null;
        }

        if (invite.isExpired()) {
            invites.remove(player);
            return null;
        }

        return invite;
    }

    public void removeInvite(UUID player) {
        if (player == null) {
            return;
        }

        invites.remove(player);
    }

    public boolean hasInvite(UUID player) {
        return getInvite(player) != null;
    }

    public boolean hasInvite(UUID player, String clanName) {
        ClanInvite invite = getInvite(player);

        if (invite == null || clanName == null) {
            return false;
        }

        return invite.getClanName().equalsIgnoreCase(clanName);
    }

    public void clear() {
        invites.clear();
    }

    public Map<UUID, ClanInvite> getInvites() {
        return invites;
    }
}