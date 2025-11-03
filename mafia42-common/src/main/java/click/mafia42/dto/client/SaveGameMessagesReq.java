package click.mafia42.dto.client;

import java.util.List;

public record SaveGameMessagesReq(
        List<SaveGameMessageReq> messages
) {
}
