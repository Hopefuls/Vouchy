package me.hopedev.vouchy.utils.vouchermanager;

import me.hopedev.vouchy.utils.StorageUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyValidator {

    public final String key;


    public KeyValidator(String key) {
        this.key = key;
    }

    public final boolean isValid() {
        return validate();

    }




    private boolean validate() {
        ResultSet set = StorageUtils.executeQuery("SELECT * FROM voucherData WHERE voucherKey = \""+this.key+"\"");
        try {
            if (set.next()) {
                System.out.println(set.getString("roleID"));
                return true;
            } else {
                return false;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }


}
