package click.mafia42.dto.server;

import java.util.UUID;

public record VoteUserReq(
        UUID userId
) {
}
