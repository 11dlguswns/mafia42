package click.mafia42.security.util;

import click.mafia42.entity.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class JwtGenerator {
    public static String generateAccessToken(Key ACCESS_SECRET, long ACCESS_EXPIRATION, User user) {
        Long now = currentTimeMillis();

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getId().toString())
                .setExpiration(new Date(now + ACCESS_EXPIRATION))
                .signWith(ACCESS_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(Key REFRESH_SECRET, long REFRESH_EXPIRATION, User user) {
        Long now = currentTimeMillis();

        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(user.getNickname())
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(REFRESH_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    private static Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nickname", user.getNickname());
        return claims;
    }
}