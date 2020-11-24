package me.hopedev.vouchy.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StorageUtils {



    public static String parseResult(ResultSet set, int pos) {
        try {
            if (set.next()) {

                return set.getString(pos);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = DatabaseStorage.getConnection().createStatement();
            statement.setFetchSize(5000);
            return statement.executeQuery(query);


        } catch (SQLException exception) {
            exception.printStackTrace();
        return null;
        }
    }

    public static void executeUpdate(PreparedStatement statement) throws SQLException {

            statement.execute();




    }
}
