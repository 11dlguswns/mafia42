package click.mafia42.dto.server;

import click.mafia42.entity.room.GameType;

public record CreateGameRoomReq(
        String name,
        int maxPlayers,
        GameType gameType,
        String password
) {
}
