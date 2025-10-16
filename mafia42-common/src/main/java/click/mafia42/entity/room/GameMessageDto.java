package click.mafia42.entity.room;

import click.mafia42.dto.client.SaveGameMessageReq;

import java.util.Set;

public record GameMessageDto(
        SaveGameMessageReq saveGameMessageReq,
        Set<GameRoomUser> visibleChatToUsers
) {
}
