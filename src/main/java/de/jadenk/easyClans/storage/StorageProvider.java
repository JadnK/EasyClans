package de.jadenk.easyClans.storage;

import de.jadenk.easyClans.EasyClans;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.PlayerDataManager;
import org.bukkit.configuration.file.FileConfiguration;

public class StorageProvider {

    private final EasyClans plugin;
    private final ClanManager clanManager;
    private final PlayerDataManager playerDataManager;

    public StorageProvider(EasyClans plugin, ClanManager clanManager, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playerDataManager = playerDataManager;
    }

    public Storage createStorage() {
        FileConfiguration config = plugin.getConfig();

        boolean useSql = config.getBoolean("storage.use-sql", false);

        if (useSql) {
            return new SqlStorage(plugin, clanManager, playerDataManager);
        }

        return new YamlStorage(plugin, clanManager, playerDataManager);
    }
}