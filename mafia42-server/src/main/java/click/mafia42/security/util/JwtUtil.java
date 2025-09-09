package click.mafia42.security.util;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.security.enums.TokenStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class JwtUtil {
    public static TokenStatus getTokenStatus(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            throw new GlobalException(GlobalExceptionCode.MALFORMED_TOKEN, e);
        }
    }

    public static Key getSigningKey(String secretKey) {
        String encodedKey = encodedToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String toBearerToken(String token) {
        return "Bearer " + token;
    }

    public static String extractBearerToken(String token) {
        if (token == null) {
            return null;
        }

        if (!token.startsWith("Bearer ")) {
            throw new GlobalException(GlobalExceptionCode.MALFORMED_TOKEN);
        }

        return token.substring(7);
    }

    private static String encodedToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
