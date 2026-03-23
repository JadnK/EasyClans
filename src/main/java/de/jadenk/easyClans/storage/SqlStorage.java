package de.jadenk.easyClans.storage;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.clan.Clan;
import de.jadenk.easyClans.clan.ClanRole;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.PlayerDataManager;
import de.jadenk.easyClans.storage.model.PlayerData;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SqlStorage implements Storage {

    private final EasyClans plugin;
    private final ClanManager clanManager;
    private final PlayerDataManager playerDataManager;

    private Connection connection;

    public SqlStorage(EasyClans plugin, ClanManager clanManager, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void load() {
        connect();
        createTables();
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

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void connect() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            File file = new File(plugin.getDataFolder(), "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void createTables() {
        String clansTable = """
                CREATE TABLE IF NOT EXISTS clans (
                    name TEXT PRIMARY KEY,
                    owner TEXT NOT NULL,
                    description TEXT,
                    points INTEGER,
                    level INTEGER,
                    clan_chat_enabled INTEGER,
                    friendly_fire INTEGER,
                    public_join INTEGER
                );
                """;

        String membersTable = """
                CREATE TABLE IF NOT EXISTS clan_members (
                    clan_name TEXT NOT NULL,
                    uuid TEXT NOT NULL,
                    role TEXT NOT NULL,
                    PRIMARY KEY (clan_name, uuid)
                );
                """;

        String playersTable = """
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    clan_name TEXT,
                    role TEXT,
                    joined_at INTEGER,
                    last_kick_reason TEXT,
                    clan_chat_enabled INTEGER
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(clansTable);
            statement.execute(membersTable);
            statement.execute(playersTable);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void loadClans() {
        clanManager.clear();

        String clanQuery = "SELECT * FROM clans";
        String memberQuery = "SELECT * FROM clan_members WHERE clan_name = ?";

        try (
                PreparedStatement clanStatement = connection.prepareStatement(clanQuery);
                ResultSet clanResult = clanStatement.executeQuery()
        ) {
            while (clanResult.next()) {
                String name = clanResult.getString("name");
                UUID owner = UUID.fromString(clanResult.getString("owner"));

                Clan clan = new Clan(name, owner);
                clan.setDescription(clanResult.getString("description"));
                clan.setPoints(clanResult.getInt("points"));
                clan.setLevel(clanResult.getInt("level"));

                clan.getSettings().setClanChatEnabled(clanResult.getInt("clan_chat_enabled") == 1);
                clan.getSettings().setFriendlyFire(clanResult.getInt("friendly_fire") == 1);
                clan.getSettings().setPublicJoin(clanResult.getInt("public_join") == 1);

                clan.getMembers().clear();

                try (PreparedStatement memberStatement = connection.prepareStatement(memberQuery)) {
                    memberStatement.setString(1, name);

                    try (ResultSet memberResult = memberStatement.executeQuery()) {
                        while (memberResult.next()) {
                            UUID memberId = UUID.fromString(memberResult.getString("uuid"));
                            ClanRole role = ClanRole.valueOf(memberResult.getString("role").toUpperCase());
                            clan.getMembers().put(memberId, role);
                        }
                    }
                }

                if (!clan.getMembers().containsKey(owner)) {
                    clan.getMembers().put(owner, ClanRole.OWNER);
                }

                clanManager.registerClan(clan);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void saveClans() {
        String deleteMembers = "DELETE FROM clan_members";
        String deleteClans = "DELETE FROM clans";

        String insertClan = """
                INSERT INTO clans(name, owner, description, points, level, clan_chat_enabled, friendly_fire, public_join)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertMember = """
                INSERT INTO clan_members(clan_name, uuid, role)
                VALUES(?, ?, ?)
                """;

        try (
                Statement statement = connection.createStatement();
                PreparedStatement clanStatement = connection.prepareStatement(insertClan);
                PreparedStatement memberStatement = connection.prepareStatement(insertMember)
        ) {
            statement.executeUpdate(deleteMembers);
            statement.executeUpdate(deleteClans);

            for (Clan clan : clanManager.getAllClans()) {
                clanStatement.setString(1, clan.getName());
                clanStatement.setString(2, clan.getOwner().toString());
                clanStatement.setString(3, clan.getDescription());
                clanStatement.setInt(4, clan.getPoints());
                clanStatement.setInt(5, clan.getLevel());
                clanStatement.setInt(6, clan.getSettings().isClanChatEnabled() ? 1 : 0);
                clanStatement.setInt(7, clan.getSettings().isFriendlyFire() ? 1 : 0);
                clanStatement.setInt(8, clan.getSettings().isPublicJoin() ? 1 : 0);
                clanStatement.executeUpdate();

                for (var entry : clan.getMembers().entrySet()) {
                    memberStatement.setString(1, clan.getName());
                    memberStatement.setString(2, entry.getKey().toString());
                    memberStatement.setString(3, entry.getValue().name());
                    memberStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void loadPlayers() {
        playerDataManager.clear();

        String query = "SELECT * FROM players";

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));

                PlayerData playerData = new PlayerData(uuid);
                playerData.setClanName(resultSet.getString("clan_name"));
                playerData.setRole(ClanRole.valueOf(resultSet.getString("role")));
                playerData.setJoinedAt(resultSet.getLong("joined_at"));
                playerData.setClanChatEnabled(resultSet.getInt("clan_chat_enabled") == 1);

                playerDataManager.save(playerData);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void savePlayers() {
        String deletePlayers = "DELETE FROM players";

        String insertPlayer = """
                INSERT INTO players(uuid, clan_name, role, joined_at, clan_chat_enabled)
                VALUES(?, ?, ?, ?, ?)
                """;

        try (
                Statement deleteStatement = connection.createStatement();
                PreparedStatement insertStatement = connection.prepareStatement(insertPlayer)
        ) {
            deleteStatement.executeUpdate(deletePlayers);

            for (PlayerData playerData : playerDataManager.getAll()) {
                insertStatement.setString(1, playerData.getUuid().toString());
                insertStatement.setString(2, playerData.getClanName());
                insertStatement.setString(3, playerData.getRole().name());
                insertStatement.setLong(4, playerData.getJoinedAt());
                insertStatement.setInt(5, playerData.isClanChatEnabled() ? 1 : 0);
                insertStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}