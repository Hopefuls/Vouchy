package me.hopedev.vouchy.utils.vouchermanager;

import me.hopedev.vouchy.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoucherManager {


    private final String voucherKey;
    private final CommandMessage message;

    public VoucherManager(String voucherKey, CommandMessage message) {
        this.voucherKey = voucherKey;
        this.message = message;
    }


    public final boolean isValid() {
        KeyValidator validator = new KeyValidator(this.voucherKey);
        return validator.isValid();
    }


    public final Voucher getVoucher() {
        ResultSet set = StorageUtils.executeQuery("SELECT * FROM voucherData WHERE voucherKey = \"" + this.voucherKey + "\"");

        try {
            if (!this.isValid()) {
                return null;
            }
            if (set.next()) {

                return new Voucher(this.voucherKey, set.getLong("roleID"), set.getLong("creatorID"), set.getLong("serverID"), set.getLong("createdAt"), this.message);
            } else {
                return null;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
            return null;
    }



    public static String createKey(String serverID, String creatorID, String roleID, CommandMessage message) {

        String key = new RandomString(15).nextString();

        try {

            PreparedStatement stmt = DatabaseStorage.getConnection().prepareStatement("insert into voucherData (voucherKey, serverID, roleID, creatorID, createdAt, expired) values (?, ?, ?, ?, ?, ?);");

            stmt.setString(1, key);
            stmt.setLong(2, Long.parseLong(serverID));
            stmt.setLong(3, Long.parseLong(roleID));
            stmt.setLong(4, Long.parseLong(creatorID));
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setBoolean(6, false);
            stmt.execute();

        } catch (SQLException exception) {
            EmbedTemplates.sendError(exception, message);
        }
        return key;
    }

    }


