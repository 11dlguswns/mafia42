package click.mafia42.database.dao;

import click.mafia42.entity.refresh_token.RefreshToken;
import click.mafia42.database.transaction.TransactionManager;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class RefreshTokenDao {
    public Optional<RefreshToken> findByUserId(UUID userId) {
        Connection connection = TransactionManager.getConnection();
        String sql = "SELECT u.nickname, u.password, rt.token " +
                "FROM refresh_token rt " +
                "JOIN users u ON rt.user_id = u.user_id " +
                "WHERE rt.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nickname = rs.getString("nickname");
                String password = rs.getString("password");
                User user = new User(userId, nickname, password);

                String token = rs.getString("token");
                return Optional.of(new RefreshToken(user, token));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }

    public Optional<RefreshToken> findByUserEmail(String email) {
        Connection connection = TransactionManager.getConnection();
        String sql = "SELECT u.user_id u.nickname, u.password, rt.token " +
                "FROM refresh_token rt " +
                "JOIN users u ON rt.user_id = u.user_id " +
                "WHERE u.email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID userId = UUID.fromString(rs.getString("id"));
                String nickname = rs.getString("nickname");
                String password = rs.getString("password");
                User user = new User(userId, nickname, password);

                String token = rs.getString("token");
                return Optional.of(new RefreshToken(user, token));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }

    public void save(RefreshToken refreshToken) {
        Connection connection = TransactionManager.getConnection();
        String sql = "INSERT INTO refresh_token (user_id, token) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, refreshToken.getUser().getId().toString());
            stmt.setString(2, refreshToken.getToken());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }

    public void update(UUID userId, String refreshToken) {
        Connection connection = TransactionManager.getConnection();
        String sql = "UPDATE refresh_token rt SET rt.token = ? WHERE rt.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, refreshToken);
            stmt.setString(2, userId.toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GlobalException(GlobalExceptionCode.DB_DISCONNECTION_FAIL, e);
        }
    }
}
