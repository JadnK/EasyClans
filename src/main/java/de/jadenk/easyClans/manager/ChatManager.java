package de.jadenk.easyClans.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {

    private final Set<UUID> clanChatPlayers;

    public ChatManager() {
        this.clanChatPlayers = new HashSet<>();
    }

    public boolean toggleClanChat(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        if (clanChatPlayers.contains(uuid)) {
            clanChatPlayers.remove(uuid);
            return false;
        }

        clanChatPlayers.add(uuid);
        return true;
    }

    public boolean isInClanChat(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        return clanChatPlayers.contains(uuid);
    }

    public void enableClanChat(UUID uuid) {
        if (uuid == null) {
            return;
        }

        clanChatPlayers.add(uuid);
    }

    public void disableClanChat(UUID uuid) {
        if (uuid == null) {
            return;
        }

        clanChatPlayers.remove(uuid);
    }

    public void clear() {
        clanChatPlayers.clear();
    }

    public Set<UUID> getClanChatPlayers() {
        return clanChatPlayers;
    }
}