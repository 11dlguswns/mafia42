package click.mafia42.security.service.dto;

import java.time.LocalDateTime;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresIn,
        LocalDateTime refreshTokenExpiresIn
) {
}