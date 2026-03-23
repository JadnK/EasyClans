package de.jadenk.easyClans.storage.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanData {

    private String name;
    private UUID owner;
    private Map<UUID, String> members;

    private String description;
    private int points;
    private int level;

    private boolean clanChatEnabled;
    private boolean friendlyFire;
    private boolean publicJoin;

    public ClanData() {
        this.members = new HashMap<>();
        this.description = "";
        this.points = 0;
        this.level = 1;
        this.clanChatEnabled = true;
        this.friendlyFire = false;
        this.publicJoin = false;
    }

    public ClanData(String name, UUID owner) {
        this();
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Map<UUID, String> getMembers() {
        return members;
    }

    public void setMembers(Map<UUID, String> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isClanChatEnabled() {
        return clanChatEnabled;
    }

    public void setClanChatEnabled(boolean clanChatEnabled) {
        this.clanChatEnabled = clanChatEnabled;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public boolean isPublicJoin() {
        return publicJoin;
    }

    public void setPublicJoin(boolean publicJoin) {
        this.publicJoin = publicJoin;
    }
}