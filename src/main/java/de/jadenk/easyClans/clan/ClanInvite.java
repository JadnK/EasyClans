package de.jadenk.easyClans.clan;

import java.util.UUID;

public class ClanInvite {

    private final String clanName;
    private final UUID invitedPlayer;
    private final UUID invitedBy;
    private final long expiresAt;

    public ClanInvite(String clanName, UUID invitedPlayer, UUID invitedBy) {
        this(clanName, invitedPlayer, invitedBy, 0L);
    }

    public ClanInvite(String clanName, UUID invitedPlayer, UUID invitedBy, long expiresAt) {
        this.clanName = clanName;
        this.invitedPlayer = invitedPlayer;
        this.invitedBy = invitedBy;
        this.expiresAt = expiresAt;
    }

    public String getClanName() {
        return clanName;
    }

    public UUID getInvitedPlayer() {
        return invitedPlayer;
    }

    public UUID getInvitedBy() {
        return invitedBy;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        if (expiresAt <= 0) return false;
        return System.currentTimeMillis() > expiresAt;
    }
}