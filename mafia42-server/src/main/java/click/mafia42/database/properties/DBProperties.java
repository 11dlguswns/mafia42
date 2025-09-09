package click.mafia42.database.properties;

import click.mafia42.exception.GlobalExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBProperties {
    private static final Logger log = LoggerFactory.getLogger(DBProperties.class);
    public static final String DB_URL;
    public static final String DB_USERNAME;
    public static final String DB_PASSWORD;

    static {
        String dbUrl = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            log.error(GlobalExceptionCode.NOT_FOUND_ENV.getMessage());
        }

        DB_URL = dbUrl;
        DB_USERNAME = dbUsername;
        DB_PASSWORD = dbPassword;
    }
}
