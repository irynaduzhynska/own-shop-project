package shop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find MySql Driver!", e);
        }
    }

    public static Connection getConnection() {
        Properties dbProperties = new Properties();
        dbProperties.put("user", "user_name");
        dbProperties.put("password", "user_password");
        String url = "jdbc:mysql://localhost:3306/internet_shop?serverTimezone=UTC";
        try {
            Connection connection = DriverManager.getConnection(url, dbProperties);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Can't establish connection to DB!", e);
        }
    }
}
