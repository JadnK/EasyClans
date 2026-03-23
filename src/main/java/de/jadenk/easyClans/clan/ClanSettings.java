package de.jadenk.easyClans.clan;

public class ClanSettings {

    private boolean clanChatEnabled;
    private boolean friendlyFire;
    private boolean publicJoin;

    public ClanSettings() {
        this.clanChatEnabled = true;
        this.friendlyFire = false;
        this.publicJoin = false;
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