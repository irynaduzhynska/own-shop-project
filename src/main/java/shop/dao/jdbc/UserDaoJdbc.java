package shop.dao.jdbc;

import shop.dao.UserDao;
import shop.exceptions.DataProcessingException;
import shop.lib.Dao;
import shop.model.Role;
import shop.model.User;
import shop.util.ConnectionUtil;

import java.sql.*;
import java.util.*;

@Dao
public class UserDaoJdbc implements UserDao {

    @Override
    public Optional<User> findByLogin(String login) {
        String selectQuery = "SELECT * FROM users WHERE login = ? AND is_deleted = FALSE";
        User user = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = convertToUser(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find User with login: " + login, e);
        }
        if (user != null) {
            user.setRoles(getUserRoles(user.getId()));
        }
        return Optional.ofNullable(user);
    }

    @Override
    public User create(User user) {
        String insertQuery = "INSERT INTO users (name, login, password, salt) VALUES(?,?,?,?)";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement =
                connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            setValues(statement, user);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create User with id: " + user.getId(), e);
        }
        for (Role role : user.getRoles()) {
            Long roleId = getRoleByRoleName(role.getRoleName().toString());
            addUserRoles(user.getId(), roleId);
        }
        return user;
    }

    @Override
    public User update(User user) {
        String updateQuery = "UPDATE users SET name = ?, login = ?, password = ?, salt = ? "
                + "WHERE user_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            setValues(statement, user);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create User with id: " + user.getId(), e);
        }
        deleteUserRole(user.getId());
        for (Role role : user.getRoles()) {
            addUserRoles(user.getId(), role.getId());
        }
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        String selectQuery = "SELECT * FROM users WHERE user_id = ? AND is_deleted = FALSE";
        User user = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = convertToUser(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find User with id: " + id, e);
        }
        if (user != null) {
            user.setRoles(getUserRoles(user.getId()));
        }
        return Optional.ofNullable(user);
    }

    @Override
    public boolean delete(Long id) {
        String updateQuery = "UPDATE users SET is_deleted = TRUE WHERE user_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete User with id: " + id, e);
        }
    }

    @Override
    public List<User> getAll() {
        String selectQuery = "SELECT * FROM users WHERE is_deleted = FALSE";
        List<User> users = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = convertToUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Users from DB", e);
        }
        for (User user : users) {
            user.setRoles(getUserRoles(user.getId()));
        }
        return users;
    }

    private void setValues(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getLogin());
        statement.setString(3, user.getPassword());
        statement.setBytes(4, user.getSalt());
    }

    private User convertToUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("user_id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        byte[] salt = resultSet.getBytes("salt");
        return new User(id, name, login, password, salt);
    }

    private void addUserRoles(long userId, long roleId) {
        String insertQuery = "INSERT INTO user_roles (user_id, role_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setLong(1, userId);
            statement.setLong(2, roleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add user_role", e);
        }
    }

    private Set<Role> getUserRoles(long userId) {
        String selectQuery = "SELECT r.role_id, r.role_name FROM roles r "
                + "INNER JOIN user_roles ur ON r.role_id = ur.role_id WHERE ur.user_id = ?";
        Set<Role> roles = new HashSet<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Role role = Role.of(resultSet.getString("role_name"));
                role.setId(resultSet.getLong("role_id"));
                roles.add(role);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get userRoles", e);
        }
        return roles;
    }

    private Long getRoleByRoleName(String roleName) {
        String selectQuery = "SELECT role_id FROM roles WHERE role_name = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, roleName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("role_id");
            }
            throw new DataProcessingException("");
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get role by roleName: " + roleName, e);
        }
    }

    private void deleteUserRole(Long userId) {
        String deleteQuery = "DELETE FROM user_roles WHERE user_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete userRole by userId: " + userId, e);
        }
    }
}
