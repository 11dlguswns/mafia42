package click.mafia42.dto.client;

import java.util.UUID;

public record SaveGameMessageReq(
        UUID userId,
        String message
) {
}
