package me.hopedev.vouchy.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseStorage {

    private static Connection connection;

    public static void start(String hostname, String database, String username, String password) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://"+hostname+":3306/"+database+"?autoReconnect=true";

            connection = DriverManager.getConnection(url, username, password);
            System.out.println(StorageUtils.parseResult(connection.createStatement().executeQuery("SELECT VERSION()"), 1));

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
       try{
           connection.createStatement().executeQuery("select VERSION()");
       } catch (SQLException exception) {
           return getConnection();
       }
        return connection;
    }

}
