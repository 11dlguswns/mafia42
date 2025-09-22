package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameType;

public record SaveGameRoomReq(
        long id,
        String name,
        int maxPlayers,
        int playersCount,
        GameType gameType
) {
    public static SaveGameRoomReq from(GameRoom gameRoom) {
        return new SaveGameRoomReq(
                gameRoom.getId(),
                gameRoom.getName(),
                gameRoom.getMaxPlayers(),
                gameRoom.getPlayersCount(),
                gameRoom.getGameType()
        );
    }
}
