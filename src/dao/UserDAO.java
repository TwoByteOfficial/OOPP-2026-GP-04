package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ==============================
    // CREATE
    // ==============================

    public boolean addUser(User user) {

        String sql = """
                INSERT INTO users
                (
                    username,
                    password_hash,
                    full_name,
                    email,
                    contact_no,
                    address,
                    profile_picture,
                    department_id,
                    user_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getContactNo());
            statement.setString(6, user.getAddress());
            statement.setString(7, user.getProfilePicture());

            if (user.getDepartmentId() != null) {
                statement.setInt(8, user.getDepartmentId());
            } else {
                statement.setNull(8, Types.INTEGER);
            }

            statement.setString(9, user.getUserStatus());

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error adding user:");
            e.printStackTrace();
            return false;
        }
    }


    // ==============================
    // READ ALL
    // ==============================

    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    email,
                    contact_no,
                    address,
                    profile_picture,
                    department_id,
                    user_status
                FROM users
                ORDER BY user_id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                User user = mapResultSetToUser(resultSet);

                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users:");
            e.printStackTrace();
        }

        return users;
    }


    // ==============================
    // READ BY ID
    // ==============================

    public User getUserById(long userId) {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    email,
                    contact_no,
                    address,
                    profile_picture,
                    department_id,
                    user_status
                FROM users
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user:");
            e.printStackTrace();
        }

        return null;
    }


    // ==============================
    // READ BY USERNAME
    // ==============================

    public User getUserByUsername(String username) {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    email,
                    contact_no,
                    address,
                    profile_picture,
                    department_id,
                    user_status
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user:");
            e.printStackTrace();
        }

        return null;
    }


    // ==============================
    // UPDATE
    // ==============================

    public boolean updateUser(User user) {

        String sql = """
                UPDATE users
                SET
                    username = ?,
                    full_name = ?,
                    email = ?,
                    contact_no = ?,
                    address = ?,
                    profile_picture = ?,
                    department_id = ?,
                    user_status = ?
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getContactNo());
            statement.setString(5, user.getAddress());
            statement.setString(6, user.getProfilePicture());

            if (user.getDepartmentId() != null) {
                statement.setInt(7, user.getDepartmentId());
            } else {
                statement.setNull(7, Types.INTEGER);
            }

            statement.setString(8, user.getUserStatus());
            statement.setLong(9, user.getUserId());

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user:");
            e.printStackTrace();
            return false;
        }
    }


    // ==============================
    // UPDATE PASSWORD
    // ==============================

    public boolean updatePassword(long userId, String passwordHash) {

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, passwordHash);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating password:");
            e.printStackTrace();
            return false;
        }
    }


    // ==============================
    // DELETE
    // ==============================

    public boolean deleteUser(long userId) {

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user:");
            e.printStackTrace();
            return false;
        }
    }


    // ==============================
    // HELPER METHOD
    // ==============================

    private User mapResultSetToUser(ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getLong("user_id")
        );

        user.setUsername(
                resultSet.getString("username")
        );

        user.setPasswordHash(
                resultSet.getString("password_hash")
        );

        user.setFullName(
                resultSet.getString("full_name")
        );

        user.setEmail(
                resultSet.getString("email")
        );

        user.setContactNo(
                resultSet.getString("contact_no")
        );

        user.setAddress(
                resultSet.getString("address")
        );

        user.setProfilePicture(
                resultSet.getString("profile_picture")
        );

        int departmentId =
                resultSet.getInt("department_id");

        if (resultSet.wasNull()) {
            user.setDepartmentId(null);
        } else {
            user.setDepartmentId(departmentId);
        }

        user.setUserStatus(
                resultSet.getString("user_status")
        );

        return user;
    }
}