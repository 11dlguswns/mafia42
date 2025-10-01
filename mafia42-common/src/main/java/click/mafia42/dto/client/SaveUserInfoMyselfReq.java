package click.mafia42.dto.client;

import java.util.UUID;

public record SaveUserInfoMyselfReq(
        UUID id,
        String nickname
) {
}
