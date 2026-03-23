package de.jadenk.easyClans;

import de.jadenk.easyClans.commands.ClanCommand;
import de.jadenk.easyClans.gui.GuiManager;
import de.jadenk.easyClans.listener.AnvilGuiListener;
import de.jadenk.easyClans.listener.GuiListener;
import de.jadenk.easyClans.manager.ClanManager;
import de.jadenk.easyClans.manager.InviteManager;
import de.jadenk.easyClans.manager.PlayerDataManager;
import de.jadenk.easyClans.service.ClanService;
import de.jadenk.easyClans.storage.Storage;
import de.jadenk.easyClans.storage.StorageProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyClans extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private Storage storage;

    private GuiManager guiManager;
    private ClanManager clanManager;
    private InviteManager inviteManager;
    private ClanService clanService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.guiManager = new GuiManager();
        this.clanManager = new ClanManager();
        this.inviteManager = new InviteManager();
        this.playerDataManager = new PlayerDataManager();
        this.clanService = new ClanService(this, clanManager, inviteManager, playerDataManager);

        StorageProvider storageProvider = new StorageProvider(this, clanManager, playerDataManager);
        this.storage = storageProvider.createStorage();
        this.storage.load();

        getCommand("clan").setExecutor(new ClanCommand(this));
        getServer().getPluginManager().registerEvents(new AnvilGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.close();
        }
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public ClanService getClanService() {
        return clanService;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public Storage getStorage() {
        return storage;
    }
}