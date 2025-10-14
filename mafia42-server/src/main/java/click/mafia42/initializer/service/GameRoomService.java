package click.mafia42.initializer.service;

import click.mafia42.database.ChannelManager;
import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.client.*;
import click.mafia42.dto.server.*;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Optional;

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
        String password = getPassword(request);

        if (gameRoomManager.isUserInAnyGameRoom(user)) {
            throw new GlobalException(GlobalExceptionCode.ALREADY_JOINED_ROOM);
        }

        long gameRoomId = gameRoomManager.createGameRoom(
                request.name(),
                request.maxPlayers(),
                user,
                request.gameType(),
                password
        );
        GameRoom gameRoom = gameRoomManager.findById(gameRoomId)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_ROOM));

        return new Payload(Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom));
    }

    private String getPassword(CreateGameRoomReq request) {
        if (request.password().isBlank()) {
            return null;
        }

        return request.password();
    }

    public Payload joinGameRoom(JoinGameRoomReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findById(request.gameRoomId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_ROOM));

        if (gameRoomManager.isUserInAnyGameRoom(user)) {
            throw new GlobalException(GlobalExceptionCode.ALREADY_JOINED_ROOM);
        }

        gameRoom.addPlayer(user, request.password());

        saveGameRoomToGameRoomUsers(gameRoom);

        Payload SaveSystemMessagePayloadToGameRoomUsers = new Payload(
                Commend.SAVE_GAME_ROOM_LOBBY_SYSTEM_MESSAGE,
                new SaveGameRoomLobbySystemMessageReq(user.getNickname() + "님이 입장하셨습니다"));
        sendCommendToGameRoomUsers(gameRoom, SaveSystemMessagePayloadToGameRoomUsers);

        return new Payload(Commend.NOTHING, null);
    }

    public Payload fetchGameRooms(FetchGameRoomsReq fetchGameRoomsReq) {
        List<GameRoom> gameRooms = gameRoomManager.findAll();

        SaveGameRoomListReq body = new SaveGameRoomListReq(
                gameRooms.stream()
                        .map(SaveGameRoomReq::from)
                        .toList()
        );
        return new Payload(Commend.SAVE_GAME_ROOM_LIST, body);
    }

    public Payload exitGameRoomMyself(ExitGameRoomReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        exitGameRoom(gameRoomUser, gameRoom, ExitType.SELF);

        return new Payload(Commend.REMOVE_GAME_ROOM, new RemoveGameRoomReq());
    }

    public Payload kickOutGameRoomUser(KickOutGameRoomUserReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        if (!gameRoom.isManager(gameRoomUser)) {
            throw new GlobalException(GlobalExceptionCode.ROOM_MANAGE_NOT_ALLOWED);
        }
        if (gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }
        if (user.getId().equals(request.userId())) {
            throw new GlobalException(GlobalExceptionCode.CANNOT_KICK_SELF);
        }

        GameRoomUser kickOutUser = gameRoom.getPlayer(request.userId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_FOUND_USER));

        exitGameRoom(kickOutUser, gameRoom, ExitType.KICKED);

        Payload payloadToKickOutUser = new Payload(Commend.REMOVE_GAME_ROOM, new RemoveGameRoomReq());
        channelManager.sendCommendToUser(kickOutUser.getUser(), payloadToKickOutUser);

        return new Payload(Commend.NOTHING, null);
    }

    public Payload startGame(StartGameReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser currentGameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        if (gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }

        if (!currentGameRoomUser.equals(gameRoom.getManager())) {
            throw new GlobalException(GlobalExceptionCode.ROOM_MANAGE_NOT_ALLOWED);
        }

        gameRoomManager.startGame(gameRoom);

        saveGameRoomToGameRoomUsers(gameRoom);

        return new Payload(Commend.NOTHING, null);
    }

    private void saveGameRoomToGameRoomUsers(GameRoom gameRoom) {
        gameRoom.getPlayers().forEach(gameRoomUser -> {
            Payload payload = new Payload(
                    Commend.SAVE_GAME_ROOM,
                    SaveDetailGameRoomReq.from(gameRoom, gameRoomUser.getUser().getId()));
            channelManager.sendCommendToUser(gameRoomUser.getUser(), payload);
        });
    }

    public void exitGameRoomOnDisconnect(User user, ExitType exitType) {
        Optional<GameRoom> optionalGameRoom = gameRoomManager.findGameRoomByGameRoomUser(user);
        if (optionalGameRoom.isEmpty()) {
            return;
        }

        GameRoom gameRoom = optionalGameRoom.get();

        if (gameRoom.isStarted()) {
            return;
        }

        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        exitGameRoom(gameRoomUser, gameRoom, exitType);
    }

    public void exitGameRoom(GameRoomUser gameRoomUser, GameRoom gameRoom, ExitType exitType) {
        gameRoomManager.exitGameRoom(gameRoom, gameRoomUser);

        saveGameRoomToGameRoomUsers(gameRoom);

        Payload SaveSystemMessagePayloadToGameRoomUsers = new Payload(
                Commend.SAVE_GAME_ROOM_LOBBY_SYSTEM_MESSAGE,
                new SaveGameRoomLobbySystemMessageReq(gameRoomUser.getUser().getNickname() + exitType.getMessage()));
        sendCommendToGameRoomUsers(gameRoom, SaveSystemMessagePayloadToGameRoomUsers);
    }

    public void sendCommendToGameRoomUsers(GameRoom gameRoom, Payload payload) {
        List<Channel> userChannelByJoinGameRoom = channelManager.findChannelByGameRoom(gameRoom);

        channelManager.sendCommendToUsers(userChannelByJoinGameRoom, payload);
    }

    public Payload sendMessageToGameRoomLobby(SendMessageToGameRoomLobbyReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        if (gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }
        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        Payload payload = new Payload(
                Commend.SAVE_GAME_ROOM_LOBBY_MESSAGE,
                new SaveGameRoomLobbyMessageReq(SaveGameRoomUserReq.from(gameRoomUser), request.message()));
        sendCommendToGameRoomUsers(gameRoom, payload);

        return new Payload(Commend.NOTHING, null);
    }
}
