package de.jadenk.easyClans.service;

import de.jadenk.easyClans.clan.Clan;

import java.util.UUID;

public class PermissionService {

    public boolean canInvite(Clan clan, UUID uuid) {
        if (clan == null || uuid == null) {
            return false;
        }

        return clan.isOwner(uuid) || clan.isModerator(uuid);
    }

    public boolean canKick(Clan clan, UUID executor, UUID target) {
        if (clan == null || executor == null || target == null) {
            return false;
        }

        if (!clan.isMember(executor) || !clan.isMember(target)) {
            return false;
        }

        if (clan.isOwner(target)) {
            return false;
        }

        return clan.isOwner(executor) || clan.isModerator(executor);
    }

    public boolean canPromote(Clan clan, UUID executor, UUID target) {
        if (clan == null || executor == null || target == null) {
            return false;
        }

        if (!clan.isOwner(executor)) {
            return false;
        }

        if (!clan.isMember(target)) {
            return false;
        }

        if (clan.isOwner(target)) {
            return false;
        }

        return true;
    }

    public boolean canDemote(Clan clan, UUID executor, UUID target) {
        if (clan == null || executor == null || target == null) {
            return false;
        }

        if (!clan.isOwner(executor)) {
            return false;
        }

        if (!clan.isMember(target)) {
            return false;
        }

        if (clan.isOwner(target)) {
            return false;
        }

        return true;
    }

    public boolean canDelete(Clan clan, UUID uuid) {
        if (clan == null || uuid == null) {
            return false;
        }

        return clan.isOwner(uuid);
    }

    public boolean canLeave(Clan clan, UUID uuid) {
        if (clan == null || uuid == null) {
            return false;
        }

        if (!clan.isMember(uuid)) {
            return false;
        }

        return !clan.isOwner(uuid);
    }
}