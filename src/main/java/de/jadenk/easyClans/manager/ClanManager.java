package de.jadenk.easyClans.manager;

import de.jadenk.easyClans.clan.Clan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanManager {

    private final Map<String, Clan> clansByName;
    private final Map<UUID, String> playerClanMap;

    public ClanManager() {
        this.clansByName = new HashMap<>();
        this.playerClanMap = new HashMap<>();
    }

    public Clan createClan(String name, UUID owner) {
        if (name == null || name.isBlank() || owner == null) {
            return null;
        }

        String key = name.toLowerCase();

        if (clansByName.containsKey(key)) {
            return null;
        }

        if (playerClanMap.containsKey(owner)) {
            return null;
        }

        Clan clan = new Clan(name, owner);
        clansByName.put(key, clan);
        playerClanMap.put(owner, key);

        return clan;
    }

    public void deleteClan(String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        String key = name.toLowerCase();
        Clan clan = clansByName.remove(key);

        if (clan == null) {
            return;
        }

        for (UUID memberId : clan.getMembers().keySet()) {
            playerClanMap.remove(memberId);
        }
    }

    public Clan getClan(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        return clansByName.get(name.toLowerCase());
    }

    public Clan getClanByPlayer(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        String clanName = playerClanMap.get(uuid);

        if (clanName == null) {
            return null;
        }

        return clansByName.get(clanName);
    }

    public boolean hasClan(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        return playerClanMap.containsKey(uuid);
    }

    public boolean existsClan(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        return clansByName.containsKey(name.toLowerCase());
    }

    public void addPlayerToClan(UUID uuid, String clanName) {
        if (uuid == null || clanName == null || clanName.isBlank()) {
            return;
        }

        Clan clan = getClan(clanName);

        if (clan == null) {
            return;
        }

        playerClanMap.put(uuid, clan.getName().toLowerCase());
    }

    public void removePlayerFromClan(UUID uuid) {
        if (uuid == null) {
            return;
        }

        playerClanMap.remove(uuid);
    }

    public void registerClan(Clan clan) {
        if (clan == null || clan.getName() == null || clan.getName().isBlank()) {
            return;
        }

        String key = clan.getName().toLowerCase();
        clansByName.put(key, clan);

        for (UUID memberId : clan.getMembers().keySet()) {
            playerClanMap.put(memberId, key);
        }
    }

    public void clear() {
        clansByName.clear();
        playerClanMap.clear();
    }

    public Collection<Clan> getAllClans() {
        return clansByName.values();
    }

    public Map<String, Clan> getClansByName() {
        return clansByName;
    }

    public Map<UUID, String> getPlayerClanMap() {
        return playerClanMap;
    }
}