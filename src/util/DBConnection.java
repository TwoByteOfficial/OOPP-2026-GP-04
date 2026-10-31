package util;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/unimis";
    private static final String USER = "unimisuser";
    private static final String PASSWORD = "UniMis@123";

    public static Connection con() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return con();
    }
}