package click.mafia42.initializer.service;

import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.client.SaveDetailGameRoomReq;
import click.mafia42.dto.client.SaveGameRoomListReq;
import click.mafia42.dto.client.SaveGameRoomReq;
import click.mafia42.dto.server.CreateGameRoomReq;
import click.mafia42.dto.server.FetchGameRoomsReq;
import click.mafia42.dto.server.JoinGameRoomReq;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

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

        return new Payload(null, Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
    }

    public Payload joinGameRoom(JoinGameRoomReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findById(request.gameRoomId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_ROOM));

        gameRoom.addPlayer(user, request.password());

        return new Payload(null, Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
    }

    public Payload fetchGameRooms(FetchGameRoomsReq fetchGameRoomsReq) {
        List<GameRoom> gameRooms = gameRoomManager.findAll();

        SaveGameRoomListReq body = new SaveGameRoomListReq(
                gameRooms.stream()
                        .map(SaveGameRoomReq::from)
                        .toList()
        );
        return new Payload(null, Commend.SAVE_GAME_ROOM_LIST, body);
    }
}
