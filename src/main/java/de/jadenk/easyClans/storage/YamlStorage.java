package de.jadenk.easyClans.storage;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanRole;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.PlayerDataManager;
import de.jadenk.easyClans.storage.model.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class YamlStorage implements Storage {

    private final EasyClans plugin;
    private final ClanManager clanManager;
    private final PlayerDataManager playerDataManager;

    private File clansFile;
    private File playersFile;

    private YamlConfiguration clansConfig;
    private YamlConfiguration playersConfig;

    public YamlStorage(EasyClans plugin, ClanManager clanManager, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void load() {
        createFiles();
        loadClans();
        loadPlayers();
    }

    @Override
    public void save() {
        saveClans();
        savePlayers();
    }

    @Override
    public void close() {
        save();
    }

    private void createFiles() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        clansFile = new File(plugin.getDataFolder(), "clans.yml");
        playersFile = new File(plugin.getDataFolder(), "players.yml");

        try {
            if (!clansFile.exists()) {
                clansFile.createNewFile();
            }

            if (!playersFile.exists()) {
                playersFile.createNewFile();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    private void loadClans() {
        clanManager.clear();

        ConfigurationSection clansSection = clansConfig.getConfigurationSection("clans");
        if (clansSection == null) {
            return;
        }

        for (String clanKey : clansSection.getKeys(false)) {
            String path = "clans." + clanKey;

            String name = clansConfig.getString(path + ".name");
            String ownerString = clansConfig.getString(path + ".owner");

            if (name == null || ownerString == null) {
                continue;
            }

            UUID owner;
            try {
                owner = UUID.fromString(ownerString);
            } catch (IllegalArgumentException exception) {
                continue;
            }

            Clan clan = new Clan(name, owner);

            clan.setDescription(clansConfig.getString(path + ".description", ""));
            clan.setPoints(clansConfig.getInt(path + ".points", 0));
            clan.setLevel(clansConfig.getInt(path + ".level", 1));

            clan.getSettings().setClanChatEnabled(clansConfig.getBoolean(path + ".settings.clan-chat-enabled", true));
            clan.getSettings().setFriendlyFire(clansConfig.getBoolean(path + ".settings.friendly-fire", false));
            clan.getSettings().setPublicJoin(clansConfig.getBoolean(path + ".settings.public-join", false));

            clan.getMembers().clear();

            ConfigurationSection membersSection = clansConfig.getConfigurationSection(path + ".members");
            if (membersSection != null) {
                for (String uuidString : membersSection.getKeys(false)) {
                    try {
                        UUID memberId = UUID.fromString(uuidString);
                        String roleString = membersSection.getString(uuidString, "MEMBER");

                        ClanRole role;
                        try {
                            role = ClanRole.valueOf(roleString.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            role = ClanRole.MEMBER;
                        }

                        clan.getMembers().put(memberId, role);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            if (!clan.getMembers().containsKey(owner)) {
                clan.getMembers().put(owner, ClanRole.OWNER);
            }

            clanManager.registerClan(clan);
        }
    }

    private void saveClans() {
        clansConfig.set("clans", null);

        for (Clan clan : clanManager.getAllClans()) {
            String path = "clans." + clan.getName().toLowerCase();

            clansConfig.set(path + ".name", clan.getName());
            clansConfig.set(path + ".owner", clan.getOwner().toString());
            clansConfig.set(path + ".description", clan.getDescription());
            clansConfig.set(path + ".points", clan.getPoints());
            clansConfig.set(path + ".level", clan.getLevel());

            clansConfig.set(path + ".settings.clan-chat-enabled", clan.getSettings().isClanChatEnabled());
            clansConfig.set(path + ".settings.friendly-fire", clan.getSettings().isFriendlyFire());
            clansConfig.set(path + ".settings.public-join", clan.getSettings().isPublicJoin());

            for (Map.Entry<UUID, ClanRole> entry : clan.getMembers().entrySet()) {
                clansConfig.set(path + ".members." + entry.getKey(), entry.getValue().name());
            }
        }

        try {
            clansConfig.save(clansFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void loadPlayers() {
        playerDataManager.clear();

        ConfigurationSection playersSection = playersConfig.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }

        for (String uuidString : playersSection.getKeys(false)) {
            String path = "players." + uuidString;

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException exception) {
                continue;
            }

            PlayerData playerData = new PlayerData(uuid);
            playerData.setClanName(playersConfig.getString(path + ".clan-name"));
            playerData.setRole(ClanRole.valueOf(playersConfig.getString(path + ".role", "MEMBER")));
            playerData.setJoinedAt(playersConfig.getLong(path + ".joined-at", 0L));
            playerData.setClanChatEnabled(playersConfig.getBoolean(path + ".clan-chat-enabled", false));
            playerData.setLastKickReason(playersConfig.getString(path + ".last-kick-reason"));

            playerDataManager.save(playerData);
        }
    }

    private void savePlayers() {
        playersConfig.set("players", null);

        for (PlayerData playerData : playerDataManager.getAll()) {
            String path = "players." + playerData.getUuid();

            playersConfig.set(path + ".clan-name", playerData.getClanName());
            playersConfig.set(path + ".role", playerData.getRole());
            playersConfig.set(path + ".joined-at", playerData.getJoinedAt());
            playersConfig.set(path + ".clan-chat-enabled", playerData.isClanChatEnabled());
            playersConfig.set(path + ".last-kick-reason", playerData.getLastKickReason());
        }

        try {
            playersConfig.save(playersFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}