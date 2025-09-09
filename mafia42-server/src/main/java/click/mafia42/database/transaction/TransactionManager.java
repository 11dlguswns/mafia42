package click.mafia42.database.transaction;

import click.mafia42.database.properties.DBProperties;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class TransactionManager {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DBProperties.DB_URL);
        config.setUsername(DBProperties.DB_USERNAME);
        config.setPassword(DBProperties.DB_PASSWORD);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static <T> T executeInTransaction(TransactionCallback<T> callback) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connectionHolder.set(connection);

            try {
                T result = callback.doInTransaction(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw new GlobalException(GlobalExceptionCode.TRANSACTION_FAIL, e);
            }
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.TRANSACTION_FAIL, e);
        } finally {
            connectionHolder.remove();
        }
    }

    public static Connection getConnection() {
        return connectionHolder.get();
    }
}
