package de.jadenk.easyClans.storage.model;

import de.jadenk.easyClans.clan.ClanRole;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String clanName;
    private ClanRole role;
    private long joinedAt;
    private boolean clanChatEnabled;

    // Optional / später nützlich
    private String lastKickReason;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.clanName = null;
        this.role = null;
        this.joinedAt = 0L;
        this.clanChatEnabled = false;
        this.lastKickReason = null;
    }

    public PlayerData() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public ClanRole getRole() {
        return role;
    }

    public void setRole(ClanRole role) {
        this.role = role;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    public boolean isClanChatEnabled() {
        return clanChatEnabled;
    }

    public void setClanChatEnabled(boolean clanChatEnabled) {
        this.clanChatEnabled = clanChatEnabled;
    }

    public String getLastKickReason() {
        return lastKickReason;
    }

    public void setLastKickReason(String lastKickReason) {
        this.lastKickReason = lastKickReason;
    }

    public boolean hasClan() {
        return clanName != null && !clanName.isBlank();
    }

    public void clearClanData() {
        this.clanName = null;
        this.role = null;
        this.joinedAt = 0L;
    }
}