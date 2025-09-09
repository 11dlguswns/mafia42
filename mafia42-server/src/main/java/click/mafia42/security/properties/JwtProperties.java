package click.mafia42.security.properties;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.security.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;

public class JwtProperties {
    private static final Logger log = LoggerFactory.getLogger(JwtProperties.class);
    public static final Key accessSecret;
    public static final Key refreshSecret;
    public static final long accessExpiration;
    public static final long refreshExpiration;

    static {
        String accessSecretKeyByEnv = System.getenv("ACCESS_SECRET_KEY");
        String refreshSecretKeyByEnv = System.getenv("REFRESH_SECRET_KEY");
        String accessExpirationByEnv = System.getenv("ACCESS_EXPIRATION");
        String refreshExpirationByEnv = System.getenv("REFRESH_EXPIRATION");

        if (accessSecretKeyByEnv == null || refreshSecretKeyByEnv == null ||
                accessExpirationByEnv == null || refreshExpirationByEnv == null) {
            log.error(GlobalExceptionCode.NOT_FOUND_ENV.getMessage());
            throw new GlobalException(GlobalExceptionCode.NOT_FOUND_ENV);
        }

        accessSecret = JwtUtil.getSigningKey(accessSecretKeyByEnv);
        refreshSecret = JwtUtil.getSigningKey(refreshSecretKeyByEnv);
        accessExpiration = Long.parseLong(accessExpirationByEnv);
        refreshExpiration = Long.parseLong(refreshExpirationByEnv);
    }
}
