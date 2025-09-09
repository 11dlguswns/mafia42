package click.mafia42.database.user;

import click.mafia42.database.transaction.TransactionManager;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDao {
    public Optional<User> findById(UUID userId) {
        Connection connection = TransactionManager.getConnection();
        String sql = "SELECT * FROM users u WHERE u.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID id = UUID.fromString(rs.getString("user_id"));
                String nickname = rs.getString("nickname");
                String password = rs.getString("password");
                return Optional.of(new User(id, nickname, password));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }

    public Optional<User> findByNickname(String nickname) {
        Connection connection = TransactionManager.getConnection();
        String sql = "SELECT * FROM users WHERE nickname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nickname);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID id = UUID.fromString(rs.getString("user_id"));
                String password = rs.getString("password");
                return Optional.of(new User(id, nickname, password));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }

    public void save(User user) {
        Connection connection = TransactionManager.getConnection();
        String sql = "INSERT INTO users (user_id, nickname, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getId().toString());
            stmt.setString(2, user.getNickname());
            stmt.setString(3, user.getPassword());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(UUID.fromString(rs.getString(1)));
            }
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }
}
