package click.mafia42.database.transaction;

import java.sql.Connection;

@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(Connection connection) throws Exception;
}
