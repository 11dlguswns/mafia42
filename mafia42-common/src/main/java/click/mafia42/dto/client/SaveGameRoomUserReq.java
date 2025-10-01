package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoomUser;

import java.util.UUID;

public record SaveGameRoomUserReq(
        int number,
        UUID id,
        String name
) {
    public static SaveGameRoomUserReq from(GameRoomUser gameRoomUser) {
        return new SaveGameRoomUserReq(
                gameRoomUser.getNumber(),
                gameRoomUser.getUser().getId(),
                gameRoomUser.getUser().getNickname()
        );
    }
}
