package me.hopedev.vouchy.utils.vouchermanager;

import me.hopedev.vouchy.utils.CommandMessage;
import me.hopedev.vouchy.utils.DatabaseStorage;
import me.hopedev.vouchy.utils.EmbedTemplates;
import me.hopedev.vouchy.utils.StorageUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Voucher {

    private final String voucherKey;
    private final long roleID;
    private final long creatorID;
    private final long serverID;
    private final long createdAt;
    private final CommandMessage message;
    public Voucher(String key, long roleID, long creatorID, long serverID, long createdAt, CommandMessage message) {
        this.voucherKey = key;
        this.roleID = roleID;
        this.creatorID = creatorID;
        this.serverID = serverID;
        this.createdAt = createdAt;
        this.message = message;
    }


    public final String getVoucherKey() {
        return this.voucherKey;
    }

    public final long getRoleID() {
        return this.roleID;
    }

    public final long getCreatorID() {
        return this.creatorID;
    }

    public final long getServerID() {
        return this.serverID;
    }

    public final long getCreatedAt() {
        return this.createdAt;
    }

    public final boolean isRedeemed() {
        try {
            PreparedStatement statement = DatabaseStorage.getConnection().prepareStatement("SELECT * FROM voucherData WHERE voucherKey = ?");
            statement.setString(1, this.getVoucherKey());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getBoolean("expired");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            EmbedTemplates.sendError(e, this.message);
            return false;
        }
    }

    public final void delete() {
        try {
            PreparedStatement statement = DatabaseStorage.getConnection().prepareStatement("DELETE FROM voucherData WHERE voucherKey = ?");
            statement.setString(1, this.getVoucherKey());
            statement.executeUpdate();
        } catch (Exception e) {
            EmbedTemplates.sendError(e, this.message);
        }
    }

    public final void clearAll() {
        try {
            PreparedStatement statement = DatabaseStorage.getConnection().prepareStatement("DELETE FROM voucherData WHERE serverID = ?");
            statement.setLong(1, this.getServerID());
            statement.executeUpdate();
        } catch (Exception e) {
            EmbedTemplates.sendError(e, this.message);
        }
    }

    public final void redeem() {
        try {
            PreparedStatement statement = DatabaseStorage.getConnection().prepareStatement("UPDATE voucherData SET expired = true WHERE voucherKey = ?");
            statement.setString(1, this.getVoucherKey());
            statement.executeUpdate();
        } catch (Exception e) {
            EmbedTemplates.sendError(e, this.message);
        }
    }



}
