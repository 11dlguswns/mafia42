package click.mafia42.dto;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameType;
import click.mafia42.entity.user.User;

public record SaveGameRoomReq(
        long id,
        String name,
        int maxPlayers,
        int playersCount,
        User manager,
        GameType gameType
) {
    public static SaveGameRoomReq from(GameRoom gameRoom) {
        return new SaveGameRoomReq(
                gameRoom.getId(),
                gameRoom.getName(),
                gameRoom.getMaxPlayers(),
                gameRoom.getPlayersCount(),
                gameRoom.getManager(),
                gameRoom.getGameType()
        );
    }
}
