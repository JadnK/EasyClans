package de.jadenk.easyClans.clan;

import java.util.UUID;

public class ClanMember {

    private UUID uuid;
    private ClanRole role;
    private long joinedAt;

    public ClanMember(UUID uuid, ClanRole role) {
        this.uuid = uuid;
        this.role = role;
        this.joinedAt = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
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
}