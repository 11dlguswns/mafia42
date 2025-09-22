package click.mafia42.dto.client;

import java.time.LocalDateTime;

public record SaveTokenReq(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresIn,
        LocalDateTime refreshTokenExpiresIn
) {
}
