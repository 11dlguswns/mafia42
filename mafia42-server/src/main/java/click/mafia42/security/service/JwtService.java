package click.mafia42.security.service;

import click.mafia42.entity.refresh_token.RefreshToken;
import click.mafia42.database.dao.RefreshTokenDao;
import click.mafia42.entity.user.User;
import click.mafia42.database.dao.UserDao;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.security.enums.TokenStatus;
import click.mafia42.security.properties.JwtProperties;
import click.mafia42.security.service.dto.TokenResponse;
import click.mafia42.security.util.JwtGenerator;
import click.mafia42.security.util.JwtUtil;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public class JwtService {
    private final UserDao userDao = new UserDao();
    private final RefreshTokenDao refreshTokenDao = new RefreshTokenDao();

    public String generateAccessToken(User requestUser) {
        return JwtUtil.toBearerToken(
                JwtGenerator.generateAccessToken(
                        JwtProperties.accessSecret,
                        JwtProperties.accessExpiration,
                        requestUser)
        );
    }

    public String generateRefreshToken(User requestUser) {
        String refreshToken = JwtGenerator.generateRefreshToken(
                JwtProperties.refreshSecret,
                JwtProperties.refreshExpiration,
                requestUser);

        Optional<RefreshToken> optionalSavedRefreshToken = refreshTokenDao.findByUserId(requestUser.getId());

        if (optionalSavedRefreshToken.isPresent()) {
            optionalSavedRefreshToken.get().updateToken(refreshToken);
            refreshTokenDao.update(requestUser.getId(), refreshToken);
        } else {
            refreshTokenDao.save(new RefreshToken(requestUser, refreshToken));
        }

        return JwtUtil.toBearerToken(refreshToken);
    }

    public TokenResponse generateTokenResponse(User requestUser) {
        return new TokenResponse(
                generateAccessToken(requestUser),
                generateRefreshToken(requestUser),
                now().plusSeconds(JwtProperties.accessExpiration / 1000),
                now().plusSeconds(JwtProperties.refreshExpiration / 1000));
    }

    public boolean validateAccessToken(String token) {
        return JwtUtil.getTokenStatus(token, JwtProperties.accessSecret) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, String email) {
        boolean isRefreshValid = JwtUtil.getTokenStatus(
                token, JwtProperties.refreshSecret) == TokenStatus.AUTHENTICATED;

        RefreshToken savedRefreshToken = refreshTokenDao.findByUserEmail(email)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_REFRESH_TOKEN));

        boolean isRefreshTokenMatch = savedRefreshToken.getToken().equals(token);

        return isRefreshValid && isRefreshTokenMatch;
    }

    public User getUserByAccessToken(String token) {
        return userDao.findById(UUID.fromString(getUserId(token, JwtProperties.accessSecret)))
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_USER));
    }

    private String getUserId(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getNicknameFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(JwtProperties.refreshSecret)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.MALFORMED_TOKEN, e);
        }
    }
}