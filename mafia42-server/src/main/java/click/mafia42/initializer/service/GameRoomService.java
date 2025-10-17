package click.mafia42.initializer.service;

import click.mafia42.database.ChannelManager;
import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.client.*;
import click.mafia42.dto.server.*;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.mafia.Thief;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static click.mafia42.initializer.handler.AuthHandler.USER;

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

        return new Payload(Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom, user.getId()));
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
        sendGameSystemMessageToGameRoomUsers(gameRoom, "밤이 되었습니다");

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

        Payload payload = new Payload(
                Commend.SAVE_GAME_ROOM_LOBBY_MESSAGE,
                new SaveGameRoomLobbyMessageReq(user.getId(), request.message()));
        sendCommendToGameRoomUsers(gameRoom, payload);

        return new Payload(Commend.NOTHING, null);
    }

    public Payload sendMessageToGame(SendMessageToGameReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        MessageType messageType = getMessageType(gameRoom, gameRoomUser);
        Set<GameRoomUser> visibleChatToUsers = new HashSet<>(getVisibleChatToUsers(gameRoom, messageType));

        if (!gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        visibleChatToUsers.addAll(gameRoom.getPlayers().stream()
                .filter(gUser -> gUser.getStatus() == GameUserStatus.DIE).toList());

        SaveGameMessageReq saveGameMessageReq = new SaveGameMessageReq(user.getId(), request.message(), messageType);
        gameRoom.addGameMessage(saveGameMessageReq, visibleChatToUsers);
        Payload payload = new Payload(
                Commend.SAVE_GAME_MESSAGE,
                saveGameMessageReq);

        visibleChatToUsers.forEach(gUser -> {
            channelManager.sendCommendToUser(gUser.getUser(), payload);
        });

        return new Payload(Commend.NOTHING, null);
    }

    private MessageType getMessageType(GameRoom gameRoom, GameRoomUser gameRoomUser) {
        if (gameRoomUser.getStatus() == GameUserStatus.DIE) {
            return MessageType.DIE;
        }

        if (gameRoom.getStatus() == GameStatus.MORNING || gameRoom.getStatus() == GameStatus.VOTING) {
            return MessageType.ALL;
        }

        if (gameRoom.getStatus() == GameStatus.CONTRADICT) {
            boolean isUserMostVoted = gameRoom.getMostVotedUser()
                    .map(mostVotedUser -> mostVotedUser.equals(gameRoomUser))
                    .orElse(false);

            if (isUserMostVoted) {
                return MessageType.ALL;
            }

            throw new GlobalException(GlobalExceptionCode.CHATTING_NOT_ALLOWED);
        }

        if (gameRoom.getStatus() == GameStatus.NIGHT) {
            switch (gameRoomUser.getJob().getJobType()) {
                case LOVER -> {
                    return MessageType.LOVER;
                }
                case PSYCHIC -> {
                    if (gameRoom.getDay() != 0) {
                        return MessageType.PSYCHIC;
                    }
                }
                case MAFIA -> {
                    return MessageType.MAFIA;
                }
                case CULT_LEADER -> {
                    return MessageType.CULT;
                }
                case FANATIC -> {
                    Optional<GameRoomUser> cultLeader = gameRoom.getPlayers().stream()
                            .filter(gUser -> gUser.getJob().getJobType() == JobType.CULT_LEADER)
                            .findFirst();

                    if (cultLeader.isEmpty()) {
                        return MessageType.CULT;
                    }
                }
                case THIEF -> {
                    if (gameRoomUser.getJob() instanceof Thief thief) {
                        Job stealJob = thief.getStealJob();

                        if (stealJob.getJobType() == JobType.LOVER) {
                            return MessageType.LOVER;
                        }
                    }
                }
            }

            throw new GlobalException(GlobalExceptionCode.CHATTING_NOT_ALLOWED);
        }

        throw new GlobalException(GlobalExceptionCode.CHATTING_NOT_ALLOWED);
    }

    public void sendGameSystemMessageToGameRoomUsers(GameRoom gameRoom, String message) {
        SaveGameMessageReq saveGameMessageReq = new SaveGameMessageReq(null, message, MessageType.SYSTEM);
        gameRoom.addGameMessage(saveGameMessageReq, gameRoom.getPlayers());

        Payload payload = new Payload(
                Commend.SAVE_GAME_MESSAGE,
                saveGameMessageReq);
        sendCommendToGameRoomUsers(gameRoom, payload);
    }

    private Set<GameRoomUser> getVisibleChatToUsers(GameRoom gameRoom, MessageType messageType) {
        Set<GameRoomUser> visibleChatToUsers = new HashSet<>();

        switch (messageType) {
            case ALL -> visibleChatToUsers.addAll(gameRoom.getPlayers());
            case LOVER -> visibleChatToUsers.addAll(gameRoom.getPlayers().stream()
                    .filter(gUser -> gUser.getJob().getJobType() == JobType.LOVER).toList());
            case PSYCHIC, DIE -> visibleChatToUsers.addAll(gameRoom.getPlayers().stream()
                    .filter(gUser -> gUser.getStatus() == GameUserStatus.DIE).toList());
            case MAFIA -> {
                List<GameRoomUser> mafiaUsers = gameRoom.getPlayers().stream()
                        .filter(gUser -> gUser.getJob().getJobType() == JobType.MAFIA).toList();
                List<GameRoomUser> contactedUsers = gameRoom.getPlayers().stream()
                        .filter(GameRoomUser::isContacted).toList();

                visibleChatToUsers.addAll(mafiaUsers);
                visibleChatToUsers.addAll(contactedUsers);
            }
            case CULT -> visibleChatToUsers.addAll(gameRoom.getPlayers().stream()
                    .filter(GameRoomUser::isProselytized).toList());
        }

        return visibleChatToUsers;
    }

    public Payload updateGameStatus(UpdateGameStatusReq updateGameStatusReq, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        if (!gameRoom.updateStatus()) {
            return new Payload(Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom, user.getId()));
        }

        switch (gameRoom.getStatus()) {
            case NIGHT -> {
                Optional<GameRoomUser> mostVotedUserOptional = gameRoom.getMostVotedUser();
                if (mostVotedUserOptional.isPresent()) {
                    if (gameRoom.isVotePassed()) {
                        GameRoomUser mostVotedUser = mostVotedUserOptional.get();
                        mostVotedUser.die();
                        sendGameSystemMessageToGameRoomUsers(gameRoom, mostVotedUser.getUser().getNickname() + "님이 처형당했습니다.");
                    } else {
                        sendGameSystemMessageToGameRoomUsers(gameRoom, "투표가 부결되었습니다.");
                    }
                }

                gameRoom.endMorningEvent();
                sendGameSystemMessageToGameRoomUsers(gameRoom, "밤이 되었습니다");
            }
            case MORNING -> {
                gameRoom.startMorningEvent();
                sendGameSystemMessageToGameRoomUsers(gameRoom, "아침이 밝았습니다");
            }
            case VOTING -> {
                gameRoom.clearVotes();
                sendGameSystemMessageToGameRoomUsers(gameRoom, "투표시간이 되었습니다");
            }
            case CONTRADICT -> {
                GameRoomUser gameRoomUser = gameRoom.getMostVotedUser().get();
                sendGameSystemMessageToGameRoomUsers(gameRoom, gameRoomUser.getUser().getNickname() + "님의 최후의 반론");
            }
            case JUDGEMENT -> {
                GameRoomUser gameRoomUser = gameRoom.getMostVotedUser().get();
                sendGameSystemMessageToGameRoomUsers(gameRoom, gameRoomUser.getUser().getNickname() + "님에 대한 찬반 투표");
            }
        }

        return new Payload(Commend.SAVE_GAME_ROOM, SaveDetailGameRoomReq.from(gameRoom, user.getId()));
    }

    public Payload increaseGameTime(IncreaseGameTimeReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        gameRoom.increaseGameTime(user);
        saveGameRoomToGameRoomUsers(gameRoom);

        sendGameSystemMessageToGameRoomUsers(gameRoom, user.getNickname() + "님이 시간을 증가시켰습니다.");

        return new Payload(Commend.NOTHING, null);
    }

    public Payload decreaseGameTime(DecreaseGameTimeReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        gameRoom.decreaseGameTime(user);
        saveGameRoomToGameRoomUsers(gameRoom);

        sendGameSystemMessageToGameRoomUsers(gameRoom, user.getNickname() + "님이 시간을 감소시켰습니다.");

        return new Payload(Commend.NOTHING, null);
    }

    public Payload voteUser(VoteUserReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser requestUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser voteUser = gameRoom.getPlayer(request.userId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        gameRoom.voteUser(requestUser, voteUser);

        saveGameRoomToGameRoomUsers(gameRoom);

        return new Payload(Commend.NOTHING, null);
    }

    public Payload voteAgree(VoteAgreeReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        gameRoom.voteAgree(gameRoomUser);

        return new Payload(Commend.NOTHING, null);
    }

    public Payload voteDisagree(VoteDisagreeReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        GameRoom gameRoom = gameRoomManager.findGameRoomByGameRoomUser(user)
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));
        GameRoomUser gameRoomUser = gameRoom.getPlayer(user.getId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        gameRoom.voteDisagree(gameRoomUser);

        return new Payload(Commend.NOTHING, null);
    }
}
