package click.mafia42.initializer.service;

import click.mafia42.database.ChannelManager;
import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.client.*;
import click.mafia42.dto.server.*;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static click.mafia42.initializer.handler.AuthHandler.*;

public class GameRoomService {
    private final GameRoomManager gameRoomManager;
    private final ChannelManager channelManager;

    public GameRoomService(GameRoomManager gameRoomManager, ChannelManager channelManager) {
        this.gameRoomManager = gameRoomManager;
        this.channelManager = channelManager;
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

        Payload payload = new Payload(null, Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
        sendCommendToGameRoomUsers(gameRoom, payload);

        return new Payload(null, Commend.NOTHING, null);
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

    public Payload exitGameRoomMyself(ExitGameRoomReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        gameRoomManager.exitGameRoom(gameRoom, user);

        Payload payload = new Payload(null, Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
        sendCommendToGameRoomUsers(gameRoom, payload);

        return new Payload(null, Commend.REMOVE_GAME_ROOM, new RemoveGameRoomReq());
    }

    private void sendCommendToGameRoomUsers(GameRoom gameRoom, Payload payload) {
        List<Channel> userChannelByJoinGameRoom = channelManager.findChannelByGameRoom(gameRoom);

        channelManager.sendCommendToUsers(userChannelByJoinGameRoom, payload);
    }

    public void exitGameRoom(GameRoom gameRoom, User user) {
        gameRoomManager.exitGameRoom(gameRoom, user);

        Payload payload = new Payload(null, Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
        sendCommendToGameRoomUsers(gameRoom, payload);
    }

    public Payload sendMessageToGameRoomLobby(SendMessageToGameRoomLobbyReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        if (gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }

        Payload payload = new Payload(
                null,
                Commend.SAVE_GAME_ROOM_LOBBY_MESSAGE,
                new SaveGameRoomLobbyMessageReq(SaveGameRoomUserReq.from(user), request.message())
        );
        sendCommendToGameRoomUsers(gameRoom, payload);

        return new Payload(null, Commend.NOTHING, null);
    }
}
