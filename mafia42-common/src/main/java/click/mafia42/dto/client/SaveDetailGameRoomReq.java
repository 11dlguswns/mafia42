package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameType;
import click.mafia42.entity.user.User;

import java.util.List;

public record SaveDetailGameRoomReq(
        long id,
        String name,
        int maxPlayers,
        List<SaveGameRoomUserReq> users,
        User manager,
        GameType gameType
) {
    public static SaveDetailGameRoomReq from(GameRoom gameRoom) {
        return new SaveDetailGameRoomReq(
                gameRoom.getId(),
                gameRoom.getName(),
                gameRoom.getMaxPlayers(),
                gameRoom.getPlayers()
                        .stream()
                        .map(SaveGameRoomUserReq::from)
                        .toList(),
                gameRoom.getManager(),
                gameRoom.getGameType()
        );
    }
}
