package click.mafia42.dto;

import java.time.LocalDateTime;

public record SaveTokenReq(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresIn,
        LocalDateTime refreshTokenExpiresIn
) {
}
