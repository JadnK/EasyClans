package de.jadenk.easyClans.clan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Clan {

    private final String name;
    private final UUID owner;

    // UUID → ROLE
    private final Map<UUID, ClanRole> members;

    private String description;
    private int points;
    private int level;

    private final ClanSettings settings;

    public Clan(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashMap<>();

        this.description = "";
        this.points = 0;
        this.level = 1;

        this.settings = new ClanSettings();

        // Owner automatisch hinzufügen
        this.members.put(owner, ClanRole.OWNER);
    }

    // =========================
    // BASIC
    // =========================
    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<UUID, ClanRole> getMembers() {
        return members;
    }

    public ClanSettings getSettings() {
        return settings;
    }

    // =========================
    // MEMBER LOGIC
    // =========================
    public void addMember(UUID uuid) {
        members.put(uuid, ClanRole.MEMBER);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public boolean isModerator(UUID uuid) {
        return members.get(uuid) == ClanRole.MODERATOR;
    }

    public ClanRole getRole(UUID uuid) {
        return members.get(uuid);
    }

    public void setRole(UUID uuid, ClanRole role) {
        if (!members.containsKey(uuid)) return;
        members.put(uuid, role);
    }

    public void promote(UUID uuid) {
        if (!members.containsKey(uuid)) return;

        ClanRole current = members.get(uuid);

        if (current == ClanRole.MEMBER) {
            members.put(uuid, ClanRole.MODERATOR);
        }
    }

    public void demote(UUID uuid) {
        if (!members.containsKey(uuid)) return;

        ClanRole current = members.get(uuid);

        if (current == ClanRole.MODERATOR) {
            members.put(uuid, ClanRole.MEMBER);
        }
    }

    // =========================
    // META
    // =========================
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
}