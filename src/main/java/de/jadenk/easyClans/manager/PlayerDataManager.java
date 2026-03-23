package de.jadenk.easyClans.manager;

import de.jadenk.easyClans.storage.model.PlayerData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public PlayerData getOrCreate(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }

    public PlayerData get(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return playerDataMap.get(uuid);
    }

    public void save(PlayerData playerData) {
        if (playerData == null || playerData.getUuid() == null) {
            return;
        }

        playerDataMap.put(playerData.getUuid(), playerData);
    }

    public void remove(UUID uuid) {
        if (uuid == null) {
            return;
        }

        playerDataMap.remove(uuid);
    }

    public boolean has(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        return playerDataMap.containsKey(uuid);
    }

    public void clear() {
        playerDataMap.clear();
    }

    public Collection<PlayerData> getAll() {
        return playerDataMap.values();
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }
}