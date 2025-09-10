package click.mafia42.initializer.service;

import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.CreateGameRoomReq;
import click.mafia42.dto.SaveGameRoomReq;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.ChannelHandlerContext;

import static click.mafia42.initializer.handler.AuthHandler.*;

public class GameRoomService {
    private final GameRoomManager gameRoomManager;

    public GameRoomService(GameRoomManager gameRoomManager) {
        this.gameRoomManager = gameRoomManager;
    }

    public Payload createGameRoom(CreateGameRoomReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();

        long gameRoomId = gameRoomManager.createGameRoom(
                request.name(),
                request.maxPlayers(),
                user,
                request.gameType(),
                request.password()
        );
        GameRoom gameRoom = gameRoomManager.findById(gameRoomId)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_ROOM));

        return new Payload(null, Commend.SAVE_GAME_ROOM, SaveGameRoomReq.from(gameRoom));
    }
}
